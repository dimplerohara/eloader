/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hcl.neo.cms.microservices.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.hcl.neo.cms.microservices.excel.schema_metadata.Attribute;
import com.hcl.neo.cms.microservices.excel.schema_metadata.Objects;
import  com.hcl.neo.cms.microservices.excel.schema_objecttype.DataType;
import  com.hcl.neo.cms.microservices.excel.schema_objecttype.PropertyInformation;
import com.hcl.neo.eloader.common.Logger;

/**
 *
 * @author sakshi_ja
 */

@Component
public class ExcelHelper {

	private static final Integer PROP_NAME_ROW_INDEX = 1;
	private static final Integer DATA_START_ROW_INDEX = 2;
	private static final Integer OBJECT_ID_COLUMN_INDEX = 0;
	private static final Integer RELATIVE_PATH_COLUMN_INDEX = 1;
	private static final Integer FORMAT_TYPE_COLUMN_INDEX = 2;
	private static final Integer OBJECT_TYPE_COLUMN_INDEX = 3;
	private static final String MULTI_VALUE_DELIMITER = "|";

	@Autowired
	private Jaxb2Marshaller jaxb2Marshaller1;

	@Autowired
	private ObjectTypeHelper objectTypeHelper;

	public Objects xmlToObject(String xmlFullPath) throws Exception{        
		return (Objects) jaxb2Marshaller1.unmarshal(new StreamSource(xmlFullPath));
	}

	public void objectToXlsx(File xlsxFile,Objects objects) throws
	FileNotFoundException, IOException { 
		if(!xlsxFile.exists()){
			xlsxFile.createNewFile();
		}
		FileUtils.copyInputStreamToFile(this.getClass().getClassLoader().getResourceAsStream("template/metadata_template.xlsx"), xlsxFile);
		InputStream input = new FileInputStream(xlsxFile);
		XSSFWorkbook wbTmpl = new XSSFWorkbook(input);
		IOUtils.closeQuietly(input);
		XSSFSheet sheet = wbTmpl.getSheetAt(0);
		Map<String, Integer> propertyMapRev
		= nameToIndexMap(sheet.getRow(PROP_NAME_ROW_INDEX));
		Integer currentRowIndex = DATA_START_ROW_INDEX;
		ProcessObject processObject;
		for ( com.hcl.neo.cms.microservices.excel.schema_metadata.Object dataObject : objects.getObject()) {
			processObject = new ProcessObject(dataObject);
			XSSFRow row = sheet.getRow(currentRowIndex);
			if (row == null) {
				row = sheet.createRow(currentRowIndex);
			}
			XSSFCell idCell = row.createCell(OBJECT_ID_COLUMN_INDEX);
			idCell.setCellValue(processObject.getId());
			Integer typeColumnIndex = propertyMapRev.get("data_object_type");
			XSSFCell objectTypeCell = row.createCell(typeColumnIndex.intValue());
			objectTypeCell.setCellValue(processObject.getType());

			Integer formatColumnIndex = propertyMapRev.get("folder_or_document");
			XSSFCell formatCell = row.createCell(formatColumnIndex.intValue());
			//formatCell.setCellValue(processObject.getFormat());

			Integer pathColumnIndex = propertyMapRev.get("object_path");
			XSSFCell pathCell = row.createCell(pathColumnIndex);
			pathCell.setCellValue(processObject.getPath());

			XSSFCell cell;
			for (PropertyInformation property : objectTypeHelper.getProperties(processObject.getType())) {
				String propertyName = property.getName();
				if (!propertyMapRev.containsKey(property.getName())) {
					continue;
				}
				Integer cellColumn;
				cellColumn = propertyMapRev.get(propertyName);
				cell = row.createCell(cellColumn);
				if (property.isIsArray()) {
					List<String> values = processObject.getPropertyValueAsList(property.getName());
					String delimitedValue = "";
					for (String value : values) {
						if (delimitedValue.length() > 0) {
							delimitedValue = delimitedValue + MULTI_VALUE_DELIMITER;
						}
						delimitedValue = delimitedValue + value;
					}
					cell.setCellValue(delimitedValue);

				} else if (property.getDatatype().equals(DataType.STRING)) {
					String value = (String) processObject.getPropertyValue(property.getName());
					cell.setCellValue(value);
				} else if (property.getDatatype().equals(DataType.OBJECT_ID)) {
					String value = (String) processObject.getPropertyValue(property.getName());
					cell.setCellValue(value);
				} else if (property.getDatatype().equals(DataType.INTEGER)) {
					String value = processObject.getPropertyValue(property.getName());
					Integer intValue = Integer.parseInt(value);
					cell.setCellValue(intValue);
				} else if (property.getDatatype().equals(DataType.DOUBLE)) {
					String value = processObject.getPropertyValue(property.getName());
					Double doubleValue = Double.parseDouble(value);
					cell.setCellValue(doubleValue);
				} else if (property.getDatatype().equals(DataType.LONG)) {
					String value = processObject.getPropertyValue(property.getName());
					Long longValue = Long.parseLong(value);
					cell.setCellValue(longValue);
				} else if (property.getDatatype().equals(DataType.SHORT)) {
					String value = processObject.getPropertyValue(property.getName());
					Integer shortValue = Integer.parseInt(value);
					cell.setCellValue(shortValue);
				} else if (property.getDatatype().equals(DataType.BOOLEAN)) {
					String value = processObject.getPropertyValue(property.getName());
					Boolean booleanValue = Boolean.parseBoolean(value);
					cell.setCellValue(booleanValue);
				} else if (property.getDatatype().equals(DataType.DATE)) {
					String value = processObject.getPropertyValue(property.getName());
					if (value != null) {
						cell.setCellValue(value);
						CellStyle style = wbTmpl.createCellStyle();
						DataFormat format = wbTmpl.createDataFormat();
						style.setDataFormat(format.getFormat("m/d/yyyy"));
						cell.setCellStyle(style);
					}
				}
			}
			currentRowIndex++;
		}

		try (FileOutputStream fileOut = new FileOutputStream(xlsxFile)) {
			wbTmpl.write(fileOut);
			IOUtils.closeQuietly(fileOut);
		}
	}

	@SuppressWarnings("rawtypes")
	public Objects xlsxToObject(MultipartFile file) throws FileNotFoundException, IOException{
		InputStream input = null;
		Objects objects = new Objects();
		try{
			input = file.getInputStream();
			XSSFWorkbook wb = new XSSFWorkbook(input);
			//IOUtils.closeQuietly(input);
			XSSFSheet sheet = wb.getSheetAt(0);

			Map<String, Integer> propertyMapRev = nameToIndexMap(sheet.getRow(PROP_NAME_ROW_INDEX));
			Logger.info(getClass(), "propertyMapRev - " + propertyMapRev);
			Map<Integer, String> propertyMap = indexToNameMap(sheet.getRow(PROP_NAME_ROW_INDEX));
			Logger.info(getClass(), "propertyMap - " + propertyMap);
			Map<String, String> typeMap = objectTypeHelper.nameToTypeMap(propertyMapRev);
			Logger.info(getClass(), "typeMap - " + typeMap);
			
			Integer lastRowNum = null;
			Iterator rows = sheet.rowIterator();
			com.hcl.neo.cms.microservices.excel.schema_metadata.Object object;
			List<com.hcl.neo.cms.microservices.excel.schema_metadata.Object> objectList = new ArrayList<>();
			Attribute attribute = null;
			
			while (rows.hasNext()) {
				object = new   com.hcl.neo.cms.microservices.excel.schema_metadata.Object();
				XSSFRow row = (XSSFRow) rows.next();

				if (lastRowNum != null && row.getRowNum() - lastRowNum > 3) {
					break;
				}
				lastRowNum = row.getRowNum();
				if (row.getRowNum() < DATA_START_ROW_INDEX) {
					continue;
				}           
				XSSFCell idCell = row.getCell(OBJECT_ID_COLUMN_INDEX);
				if(idCell ==null){
					continue;
				}
			//	Iterator cells = row.iterator();

				String cellContents;
				Boolean rowHasContent;
				String objectIdValue;
				String objectTypeValue;

				for (int cellNumber = row.getFirstCellNum(); cellNumber <= row.getLastCellNum(); cellNumber++){
					XSSFCell cell = (XSSFCell) row.getCell(cellNumber);
					if (null !=cell && !propertyMap.containsKey(cellNumber)) {
						break;
					}
					if(cell == null || cell.getCellType() == XSSFCell.CELL_TYPE_BLANK){
						cellContents = "";
						rowHasContent = true;
					} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
	                    DecimalFormat df = new DecimalFormat("#.#");
	                    cellContents = df.format(cell.getNumericCellValue());
	                    if(HSSFDateUtil.isCellDateFormatted(cell)){
	                    	cellContents = cell.getDateCellValue().toString();
	                    }
	                    rowHasContent = true;
	                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {               	
	                    cellContents = cell.getStringCellValue();
	                    rowHasContent = true;
	                } 
	                else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) { 
	                	if(cell.getBooleanCellValue()){
	                		cellContents = "true";
	                	}else{
	                		cellContents = "false";
	                	}
	                    rowHasContent = true;
	                }else {
	                    continue;
	                }
					if (rowHasContent) {
						attribute = new Attribute();
						if (cellNumber == OBJECT_ID_COLUMN_INDEX) {
							objectIdValue = cellContents;
							attribute.setName("r_object_id");
							attribute.setType("STRING");
							attribute.setValue(objectIdValue);
							object.getAttribute().add(attribute);
							continue;
						}
						if (cellNumber == OBJECT_TYPE_COLUMN_INDEX) {
							objectTypeValue = cellContents;
							attribute.setName("r_object_type");
							attribute.setType("STRING");
							attribute.setValue(objectTypeValue);
							object.getAttribute().add(attribute);
							continue;
						}

						String propertyName = propertyMap.get(cellNumber);
						String propertyType = typeMap.get(propertyName);
						if (propertyType == null) {
							continue;
						}
						/*if (cellContents.equals("")) {
							continue;
						}*/
						attribute.setName(propertyName);
						attribute.setType(propertyType);
						attribute.setValue(cellContents);
						object.getAttribute().add(attribute);
					}
				}
				objectList.add(object);
			}
			objects.getObject().addAll(objectList);
		}finally{
			if(null != input){
				input.close();
			}
		}
		return objects;
	}

	@SuppressWarnings("rawtypes")
	public Objects xlsToObject(MultipartFile file) throws FileNotFoundException, IOException {
		InputStream input = file.getInputStream();
		HSSFWorkbook wb = new HSSFWorkbook(input);
		IOUtils.closeQuietly(input);
		HSSFSheet sheet = wb.getSheetAt(0);

		Map<String, Integer> propertyMapRev = nameToIndexMap(sheet.getRow(PROP_NAME_ROW_INDEX));
		Map<Integer, String> propertyMap = indexToNameMap(sheet.getRow(PROP_NAME_ROW_INDEX));
		Map<String, String> typeMap = objectTypeHelper.nameToTypeMap(propertyMapRev);
		Objects objects = new Objects();
		Integer lastRowNum = null;
		Iterator rows = sheet.rowIterator();
		com.hcl.neo.cms.microservices.excel.schema_metadata.Object object;
		List< com.hcl.neo.cms.microservices.excel.schema_metadata.Object> objectList = new ArrayList<>();
		Attribute attribute = null;
		while (rows.hasNext()) {
			object = new  com.hcl.neo.cms.microservices.excel.schema_metadata.Object();
			HSSFRow row = (HSSFRow) rows.next();

			if (lastRowNum != null && row.getRowNum() - lastRowNum > 3) {
				break;
			}
			lastRowNum = row.getRowNum();

			if (row.getRowNum() < DATA_START_ROW_INDEX) {
				continue;
			}           
			HSSFCell idCell = row.getCell(OBJECT_ID_COLUMN_INDEX);
			if(idCell ==null){
				continue;
			}
			Iterator cells = row.cellIterator();

			String cellContents;
			Boolean rowHasContent;
			String objectIdValue;
			String objectTypeValue;

			while (cells.hasNext()) {
				HSSFCell cell = (HSSFCell) cells.next();
				if (!propertyMap.containsKey(cell.getColumnIndex())) {
					break;
				}
				if(cell == null || cell.getCellType() == XSSFCell.CELL_TYPE_BLANK){
					cellContents = "";
					rowHasContent = true;
				} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
	                    DecimalFormat df = new DecimalFormat("#.#");
	                    cellContents = df.format(cell.getNumericCellValue());
	                    if(HSSFDateUtil.isCellDateFormatted(cell)){
	                    	cellContents = cell.getDateCellValue().toString();
	                    }
	                    rowHasContent = true;
	                } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {               	
	                    cellContents = cell.getStringCellValue();
	                    rowHasContent = true;
	                } 
	                else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) { 
	                	if(cell.getBooleanCellValue()){
	                		cellContents = "true";
	                	}else{
	                		cellContents = "false";
	                	}
	                    rowHasContent = true;
	                }else {
	                    continue;
	                }
				if (rowHasContent) {
					attribute = new Attribute();
					if (cell.getColumnIndex() == OBJECT_ID_COLUMN_INDEX) {
						objectIdValue = cellContents;
						attribute.setName("r_object_id");
						attribute.setType("STRING");
						attribute.setValue(objectIdValue);
						object.getAttribute().add(attribute);
						continue;
					}
					if (cell.getColumnIndex() == OBJECT_TYPE_COLUMN_INDEX) {
						objectTypeValue = cellContents;
						attribute.setName("r_object_type");
						attribute.setType("STRING");
						attribute.setValue(objectTypeValue);
						object.getAttribute().add(attribute);
						continue;
					}

					String propertyName = propertyMap.get(cell.getColumnIndex());
					String propertyType = typeMap.get(propertyName);
					if (propertyType == null) {
						continue;
					}
					/*if (cellContents.equals("")) {
						continue;
					}*/
					attribute.setName(propertyName);
					attribute.setType(propertyType);
					attribute.setValue(cellContents);
					object.getAttribute().add(attribute);
				}
			}
			objectList.add(object);
		}
		objects.getObject().addAll(objectList);
		return objects;
	}

	 private Map<String, Integer> nameToIndexMap(HSSFRow propertyNameRow) {
	        Map<String, Integer> nameToIndexMap = new HashMap<>();
	        for (Integer cellIndex = 0;
	                cellIndex < propertyNameRow.getPhysicalNumberOfCells(); cellIndex++) {
	            HSSFCell cell = propertyNameRow.getCell(cellIndex);
	            if (cell == null) {
	                break;
	            }
	            nameToIndexMap.put(cell.getStringCellValue().trim(),
	                    cell.getColumnIndex());
	        }
	        nameToIndexMap.put("folder_or_document", FORMAT_TYPE_COLUMN_INDEX);
	        nameToIndexMap.put("object_path", RELATIVE_PATH_COLUMN_INDEX);
	        return nameToIndexMap;
	    }

	    private Map<String, Integer> nameToIndexMap(XSSFRow propertyNameRow) {
	        Map<String, Integer> nameToIndexMap = new HashMap<>();
	        for (Integer cellIndex = 0;
	                cellIndex < propertyNameRow.getPhysicalNumberOfCells(); cellIndex++) {
	            XSSFCell cell = propertyNameRow.getCell(cellIndex);
	            if (cell == null) {
	                break;
	            }
	            nameToIndexMap.put(cell.getStringCellValue().trim(),
	                    cell.getColumnIndex());
	        }
	        nameToIndexMap.put("folder_or_document", FORMAT_TYPE_COLUMN_INDEX);
	        nameToIndexMap.put("object_path", RELATIVE_PATH_COLUMN_INDEX);
	        return nameToIndexMap;
	    }

	    
	    private Map<Integer, String> indexToNameMap(HSSFRow propertyNameRow) {
	        Map<Integer, String> indexToNameMap = new HashMap<>();
	        for (Integer cellIndex = 0;
	                cellIndex < propertyNameRow.getPhysicalNumberOfCells(); cellIndex++) {
	            HSSFCell cell = propertyNameRow.getCell(cellIndex);
	            if (cell == null) {
	                break;
	            }
	            indexToNameMap.put(cell.getColumnIndex(),
	                    cell.getStringCellValue().trim());
	        }
	        indexToNameMap.put(FORMAT_TYPE_COLUMN_INDEX, "folder_or_document");
	        indexToNameMap.put(RELATIVE_PATH_COLUMN_INDEX, "object_path");
	        return indexToNameMap;
	    }

	    private Map<Integer, String> indexToNameMap(XSSFRow propertyNameRow) {
	        Map<Integer, String> indexToNameMap = new HashMap<>();
	        for (Integer cellIndex = 0;
	                cellIndex < propertyNameRow.getPhysicalNumberOfCells(); cellIndex++) {
	            XSSFCell cell = propertyNameRow.getCell(cellIndex);
	            if (cell == null) {
	                break;
	            }
	            indexToNameMap.put(cell.getColumnIndex(),
	                    cell.getStringCellValue().trim());
	        }
	        indexToNameMap.put(FORMAT_TYPE_COLUMN_INDEX, "folder_or_document");
	        indexToNameMap.put(RELATIVE_PATH_COLUMN_INDEX, "object_path");
	        return indexToNameMap;
	    }
}