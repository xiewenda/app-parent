package cn.com.taiji.sys.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

public class Sample4Dto implements Serializable {
	private static final long serialVersionUID = 6678676880061397063L;

	private String name = "ChangeMe";

	@Past
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date birth = new Date();

	
	@NotNull
	@NumberFormat
	@DecimalMin(value="55")
	private BigDecimal weight;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "Sample4Dto [name=" + name + ", birth=" + birth + ", weight="
				+ weight + "]";
	}

}
