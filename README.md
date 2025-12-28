# 聚合搜索平台

> 基于 SpringBoot 的聚合搜索系统，支持用户、帖子、图片的多维度统一搜索

## 项目简介

这是一个高性能的聚合搜索平台，提供统一的搜索接口，能够同时搜索用户、帖子和图片数据，并返回结构化的搜索结果。

## 核心功能

### 全局聚合搜索
- **统一搜索接口**：单个API端点支持多种数据类型的搜索
- **多维度搜索**：支持用户、帖子、图片的并行搜索
- **结构化结果**：返回分类清晰的搜索结果列表

## 技术架构

- **后端框架**：Spring Boot 2.7.x
- **数据访问**：MyBatis + MyBatis Plus
- **数据库**：MySQL
- **搜索实现**：基于数据库查询的全文搜索

## 快速开始

### 环境要求
- JDK 8+
- MySQL 5.7+
- Maven 3.6+

### 安装部署

1. 配置数据库连接信息
```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/search_db
    username: root
    password: 123456
```

2. 启动应用
```bash
mvn spring-boot:run
```

3. 访问接口文档
```
http://localhost:8101/api/doc.html
```

## API 使用指南

### 全局搜索接口

**接口地址**：`POST /api/search/all`

**请求参数**：
```json
{
  "searchText": "搜索关键词"
}
```

**响应示例**：
```json
{
  "code": 0,
  "data": {
    "userList": [
      {
        "id": 1,
        "userName": "张三",
        "userProfile": "用户简介"
      }
    ],
    "postList": [
      {
        "id": 1,
        "title": "帖子标题",
        "content": "帖子内容"
      }
    ],
    "pictureList": [
      {
        "id": 1,
        "title": "图片标题",
        "url": "图片地址"
      }
    ]
  },
  "message": "ok"
}
```

### 搜索范围说明

- **用户搜索**：按用户名进行模糊匹配
- **帖子搜索**：按标题、内容、标签进行全文搜索
- **图片搜索**：按图片描述和标题进行搜索

## 核心代码结构

```
src/main/java/com/yupi/springbootinit/
├── controller/
│   └── SearchController.java      # 搜索控制器
├── service/
│   ├── UserService.java           # 用户服务
│   ├── PostService.java           # 帖子服务
│   ├── PictureService.java        # 图片服务
│   └── impl/                      # 服务实现
└── model/
    ├── dto/search/                # 搜索数据传输对象
    └── vo/                        # 视图对象
```

## 搜索实现原理

### 1. 搜索控制器
位于 `SearchController.java`，处理 `/api/search/all` 请求：

```java
@RequestMapping("/all")
public BaseResponse<SearchVO> searchAll(@RequestBody SearchRequest searchRequest) {
    String searchText = searchRequest.getSearchText();
    
    // 并行搜索三种数据类型
    Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
    Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
    Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest);
    
    // 组装结果
    SearchVO searchVO = new SearchVO();
    searchVO.setUserList(userVOPage.getRecords());
    searchVO.setPostList(postVOPage.getRecords());
    searchVO.setPictureList(picturePage.getRecords());
    
    return ResultUtils.success(searchVO);
}
```

### 2. 用户搜索实现
在 `UserServiceImpl.java` 中使用 MyBatis-Plus 的 QueryWrapper 进行条件查询：

```java
@Override
public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
    int current = userQueryRequest.getCurrent();
    int size = userQueryRequest.getPageSize();
    
    // 应用查询条件
    Page<User> userPage = this.page(new Page<>(current, size), 
        this.getQueryWrapper(userQueryRequest));
    
    // 转换为视图对象
    Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
    List<UserVO> userVO = this.getUserVO(userPage.getRecords());
    userVOPage.setRecords(userVO);
    
    return userVOPage;
}
```

## 性能优化

- **分页查询**：所有搜索都支持分页，避免大数据量查询
- **条件构造**：使用 MyBatis-Plus QueryWrapper 动态构建查询条件
- **结果缓存**：支持 Redis 缓存提升搜索性能

## 故障排除

### 常见问题

1. **搜索返回所有数据**：检查查询条件是否正确应用到数据库查询
2. **搜索结果为空**：确认搜索关键词与数据匹配
3. **分页异常**：检查分页参数是否合法

### 用户搜索修复说明

**问题**：用户搜索功能返回所有用户而不是过滤结果

**修复**：在 `UserServiceImpl.listUserVOByPage` 方法中，确保查询条件正确应用到数据库查询：

```java
// 修复前：缺少查询条件
Page<User> userPage = this.page(new Page<>(current, size));

// 修复后：正确应用查询条件
Page<User> userPage = this.page(new Page<>(current, size), 
    this.getQueryWrapper(userQueryRequest));
```

## 联系方式

如有问题或建议，请联系项目维护者。