package com.yupi.springbootinit.model.dto.search;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;
    /**
     * 搜索类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}