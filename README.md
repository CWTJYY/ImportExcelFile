### 业务场景

批量导入用户数据

#### 依赖

导入操作Excel表格相关依赖

```pom
<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi</artifactId>
			<version>5.2.2</version>
		</dependency>
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi-ooxml</artifactId>
			<version>5.2.2</version>
</dependency>
```

#### 工具类

```java
package com.example.demo.util;


import com.example.demo.entity.dto.ImportExcelFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 导入Excel表格的工具类
 *
 * @author cwt
 * @date 2022/5/23 11:27
 */
public class ImportExcelUtil {

    /**
     * @param @param  excelFileName
     * @param @return
     * @param @throws IOException
     * @return Workbook
     * @throws
     * @Title: createWorkbook
     * @Description: 判断excel文件后缀名，生成不同的workbook      * @param @param is
     */
    public Workbook createWorkbook(InputStream is, String excelFileName) throws IOException {
        if (excelFileName.endsWith(".xls")) {
            return new HSSFWorkbook(is);
        } else if (excelFileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        }
        return null;
    }

    /**
     * @param @param  workbook
     * @param @param  sheetIndex
     * @param @return
     * @return Sheet
     * @throws
     * @Title: getSheet
     * @Description: 根据sheet索引号获取对应的sheet
     */
    public Sheet getSheet(Workbook workbook, int sheetIndex) {
        return workbook.getSheetAt(0);
    }


    public List<Object> importDataFromExcel(InputStream is, String excelFileName) {
        List<Object> list = new ArrayList<Object>();
        try {
            //创建工作簿
            Workbook workbook = this.createWorkbook(is, excelFileName);
            //创建工作表sheet
            Sheet sheet = this.getSheet(workbook, 0);
            //获取sheet中数据的行数
            int rows = sheet.getPhysicalNumberOfRows();
            //获取表头单元格个数
            int cells = sheet.getRow(0).getPhysicalNumberOfCells();
            //第一行为标题栏，从第二行开始取数据
            for (int i = 1; i < rows; i++) {
                //利用反射，给JavaBean的属性进行赋值
                ImportExcelFile vo = new ImportExcelFile();
                Field[] fields = vo.getClass().getDeclaredFields();

                Row row = sheet.getRow(i);
                int index = 0;
                while (index < cells) {
                    Cell cell = row.getCell(index);
                    if (null == cell) {
                        cell = row.createCell(index);
                    }
                    cell.setCellType(CellType.STRING);
                    String value = null == cell.getStringCellValue() ? "" : cell.getStringCellValue();

                    Field field = fields[index];
                    String fieldName = field.getName();
                    String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method setMethod = vo.getClass().getMethod(methodName, new Class[]{String.class});
                    setMethod.invoke(vo, new Object[]{value});
                    index++;
                }
                //判断对象属性是否有值
                if (isHasValues(vo)) {
                    list.add(vo);
                }

            }
        } catch (Exception e) {
        } finally {
            try {
                //关闭流
                is.close();
            } catch (Exception e2) {
            }
        }
        return list;

    }

    /**
     * @param @param  object
     * @param @return
     * @return boolean
     * @throws
     * @Title: isHasValues
     * @Description: 判断一个对象所有属性是否有值，如果一个属性有值(分空)，则返回true
     */
    public boolean isHasValues(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        boolean flag = false;
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getMethod;
            try {
                getMethod = object.getClass().getMethod(methodName);
                Object obj = getMethod.invoke(object);
                if (null != obj && !"".equals(obj)) {
                    flag = true;
                    break;
                }
            } catch (Exception e) {
            }
        }
        return flag;

    }
}

```

#### 实体类

```java
/**
 * Excel表格格式
 *
 * @author cwt
 */
@Data
public class ImportExcelFile {

    /**
     * 姓名
     */
    private String name;

    /**
     * 账号（手机号码）
     */
    private String mobile;
    
    /** 
     * 密码
     */
    private String password;
}
```

```java
/**
 * 用户实体类
 *
 * @author cwt
 */
@Data
public class ImportExcelFile {
    
    /**
     * id
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 账号（手机号码）
     */
    private String mobile;
    
    /** 
     * 密码
     */
    private String password;
}
```

#### 控制层

```java
    @Resource
    private Service service;

    /**
     * 批量导入Excel表格数据
     * @param file
     * @return 响应体
     * @author cwt
     * @date 
     */
    @RequestMapping(value = "/importExcel")
    public AjaxResult importExcel(MultipartFile file) throws IOException {
        service.importExcel(file);
        return AjaxResult.success();
    }
```

#### 服务层

```java
public interface Service extends IService<User> {
    /**
     * 通过excel导入用户
     *
     * @param file
     * @return java.lang.Boolean
     * @author cwt
     * @date
     */
    Boolean importExcel(MultipartFile file) throws IOException;
}
```

#### 服务实现类

```java
@Service
public class ServiceImpl extends ServiceImpl<UserMapper, User> implements Service {
    @Resource
    private UserMapper userMapper;
   
    @Override
    public Boolean importExcel(MultipartFile file) throws IOException {
        //获取文件输入流
        InputStream is = file.getInputStream();
        //创建工作蒲用于读取文件信息
        ImportExcel importExcel = new ImportExcel();
        //调用工作蒲中的方法读取文件信息,此处使用stream流进行遍历插入操作
        List<Boolean> userList = importExcel.importDataFromExcel(is, file.getOriginalFilename()).stream.map(a -> {
            //原始类型为Object，进行类型强转
            ImportExcelFile userDto = (ImportExcelFile) a;
            User user = new User();
            BeanUtils.copyBeanProp(user, userDto);
            user.setId(UUID.randomUUID().toString())
                userMapper.insert(user);
            return true;
        }).collect(Collectors.toList());
        
        return true;
    }
}
```

#### 效果

![image-20220520175157981](https://shaem-image.oss-cn-hangzhou.aliyuncs.com/imgimage-20220520175157981.png)

![image-20220520175320645](https://shaem-image.oss-cn-hangzhou.aliyuncs.com/imgimage-20220520175320645.png)

#### demo

https://github.com/CWTJYY/ImportExcelFile.git