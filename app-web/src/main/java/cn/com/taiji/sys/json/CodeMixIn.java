package cn.com.taiji.sys.json;




import java.util.List;

import cn.com.taiji.sys.domain.Code;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CodeMixIn {
	
	@JsonIgnore
	Code code;
	
	@JsonIgnore
	List<Code> codes;
}
