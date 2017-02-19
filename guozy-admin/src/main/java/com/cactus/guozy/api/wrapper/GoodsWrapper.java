package com.cactus.guozy.api.wrapper;

import java.math.BigDecimal;
import java.util.Base64;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.cactus.guozy.core.domain.Goods;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "goods")
@XmlAccessorType(value = XmlAccessType.FIELD)
@JsonInclude(Include.NON_NULL)
public class GoodsWrapper {

	@XmlElement
	private Long id;

	@XmlElement
	private String name;

	@XmlElement
	private BigDecimal price;

	@XmlElement
	private Boolean needSaler;

	@XmlElement
	private String pic;
	
	public void wrapDetails(Goods goods) {
		wrapSummary(goods);
		if(goods.getPic() != null) {
			pic = Base64.getEncoder().encodeToString(goods.getPic());
		} else {
			pic = "";
		}
	}

	public void wrapSummary(Goods goods) {
		id = goods.getId();
		name = goods.getName();
		price = goods.getPrice();
		needSaler = goods.getNeedSaler();
	}
	
	public Goods upwrap() {
		Goods goods = new Goods();
		goods.setId(getId());
		goods.setName(getName());
		goods.setNeedSaler(getNeedSaler());
		goods.setPrice(getPrice());
		if(getPic() != null && !getPic().equals("")) {
			goods.setPic(Base64.getDecoder().decode(getPic()));
		}
		return goods;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Boolean getNeedSaler() {
		return needSaler;
	}

	public void setNeedSaler(Boolean needSaler) {
		this.needSaler = needSaler;
	}
	
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

}