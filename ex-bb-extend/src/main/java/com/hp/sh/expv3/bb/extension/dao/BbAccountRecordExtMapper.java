package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbAccountRecordExtMapper {

    BbAccountRecordVo  queryOne(Map<String,Object> map);

   List<BbAccountRecordVo> queryList(Map<String,Object> map);

    List<BbAccountRecordVo> queryByIds(@Param("idList") List<Long> idList);

    List<BbAccountRecordExtVo> queryByLimit(Map<String, Object> map);

    List<BbAccountRecordExtVo> listBbAccountRecordsByPage(Map<String, Object> map);

    int existTable(@Param("dbName") String dbName, @Param("tableName") String tableName);

    Long queryCount(Map<String, Object> map);
}
