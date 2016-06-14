package cn.com.taiji.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import cn.com.taiji.common.StringUtils;
import cn.com.taiji.dao.LogsDao;
import cn.com.taiji.doman.Logs;
import cn.com.taiji.dto.LogsDto;
import cn.com.taiji.service.LogsService;


@Service
public class LogsServiceImpl implements LogsService {
  @Inject
  LogsDao logsDaoImpl;
  @Inject
  EntityManager em;

  public Map<String, Object> search(final LogsDto dto) {
    int page = 1;
    int pageSize = 10;
    if (dto.getS_page() != null && !"".equals(dto.getS_page())) {
      page = Integer.parseInt(dto.getS_page());
      if (page < 1) {
        page = 1;
      }
      pageSize = Integer.parseInt(dto.getS_pageSize());
    } else {
      dto.setS_page("1");
    }
    // 页码
    if (pageSize < 1)
      pageSize = 1;
    if (pageSize > 100)
      pageSize = 100;

    PageRequest pageable;
    /* 显示时间正序 */
    Sort s = new Sort(Direction.DESC, "createTime");
    pageable = new PageRequest(page - 1, pageSize, s);
    Specification<Logs> spec = new Specification<Logs>() {
      public Predicate toPredicate(Root<Logs> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> pl = new ArrayList<Predicate>();
        if (dto.getDomainType() != null) {
          pl.add(cb.equal(root.<String>get("domainType"), dto.getDomainType()));
        }
        if (dto.getSelectType() != null) {
          if ("year".equals(dto.getSelectType())) {
            String startYear = dto.getS_beginYear();
            String endYear = dto.getS_endYear();
            Date startTime = StringUtils.StringDateParseDate(startYear + "0101", null);
            Date endTime = StringUtils.StringDateParseDate(endYear + "0101", null);
            pl.add(cb.greaterThanOrEqualTo(root.<Date>get("createTime"), // createTime
                startTime));
            pl.add(cb.lessThanOrEqualTo(root.<Date>get("createTime"), // createTime
                endTime));
          } else if ("month".equals(dto.getSelectType())) {
            String currYear = dto.getCurrYear();
            String startMonth = dto.getS_beginMonth();
            String endMonth = dto.getS_endMonth();
            Date startTime = StringUtils.StringDateParseDate(currYear + startMonth + "01", null);
            Date endTime = StringUtils.StringDateParseDate(currYear + endMonth + "01", null);
            pl.add(cb.greaterThanOrEqualTo(root.<Date>get("createTime"), // createTime
                startTime));
            pl.add(cb.lessThanOrEqualTo(root.<Date>get("createTime"), // createTime
                endTime));
          }

        }
        return cb.and(pl.toArray(new Predicate[0]));
      }
    };

    Long total = logsDaoImpl.count(spec);
    Page<Logs> pageList = null;
    pageList = logsDaoImpl.findAll(spec, pageable);
    List<Logs> searchlist = pageList.getContent();
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("list", searchlist);
    map.put("totalPage", (total + pageSize - 1) / pageSize);
    map.put("total", total);
    return map;
  }

  public Map<String, Object> chartsStatistic(LogsDto dto) {
    // 查询统计图表的信息
    String statisticSql =
        "select :1 a,count(*) c from SYSLOG s where s.DOMAIN_TYPE = ':2' :3 GROUP BY :1 order by :1";
    String statisticTimeType = dto.getSelectType();
    String statisticModelType = dto.getDomainType();
    if (StringUtils.isNotEmpty(statisticTimeType) && StringUtils.isNotEmpty(statisticModelType)) {
      String wenhao1 = "CREATE_TIME";
      String wenhao2 = statisticModelType;
      String wenhao3 = " and 1=1";
      if ("year".equals(statisticTimeType)) {
        wenhao1 = "to_char(s.CREATE_TIME ,'YYYY')";
        String startYear = dto.getS_beginYear();
        String endYear = dto.getS_endYear();
        String startTime = "to_date(" + startYear + "0101,'YYYYMMDD')";
        String endTime = "to_date(" + endYear + "0101,'YYYYMMDD')";
        wenhao3 = "and s.CREATE_TIME>= " + startTime + " and s.CREATE_TIME<=" + endTime;
      } else if ("month".equals(statisticTimeType)) {
        wenhao1 = "to_char(s.CREATE_TIME ,'YYYYMM')";
        String currYear = dto.getCurrYear();
        wenhao3 = "and to_char(s.CREATE_TIME,'YYYY')='" + currYear + "'";
        // String startMonth = dto.getS_beginMonth();
        // String endMonth = dto.getS_endMonth();
        // String startTime = "to_date(" + currYear + startMonth + "01,'YYYYMMDD')";
        // String endTime = "to_date(" + currYear + endMonth + "01,'YYYYMMDD')";
        // wenhao3 = "and s.CREATE_TIME>= " + startTime + " and s.CREATE_TIME<=" + endTime;
      } else if ("day".equals(statisticTimeType)) {
        wenhao1 = "to_char(s.CREATE_TIME ,'YYYYMMDD')";
        String currYear = dto.getCurrYear();
        wenhao3 = "and to_char(s.CREATE_TIME,'YYYYMM')='" + currYear + "'";
      }
      statisticSql = statisticSql.replaceAll(":1", wenhao1);
      statisticSql = statisticSql.replaceAll(":2", wenhao2);
      statisticSql = statisticSql.replaceAll(":3", wenhao3);
    }
    Query query = em.createNativeQuery(statisticSql);
    @SuppressWarnings("unchecked")
    List<Object[]> ls = query.getResultList();
    List<Map<String, Object>> charDataList = new ArrayList<Map<String, Object>>();
    for (Object[] obj : ls) {
      Map<String, Object> charData = new HashMap<String, Object>();
      charData.put("time", obj[0] + "");
      charData.put("count", obj[1]);
      charDataList.add(charData);
    }
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("charData", charDataList);
    return map;
  }

}
