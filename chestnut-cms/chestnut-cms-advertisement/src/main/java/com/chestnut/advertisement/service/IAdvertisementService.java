package com.chestnut.advertisement.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chestnut.advertisement.IAdvertisementType;
import com.chestnut.advertisement.domain.CmsAdvertisement;
import com.chestnut.advertisement.pojo.dto.AdvertisementDTO;

/**
 * 广告数据管理Service
 */
public interface IAdvertisementService extends IService<CmsAdvertisement> {

	/**
	 * 广告<ID, NAME>缓存集合
	 * 
	 * @return Map
	 */
	Map<String, String> getAdvertisementMap();
	
	/**
	 * 添加广告数据
	 * 
	 * @param dto 广告数据DTO
	 * @return CmsAdvertisement
	 */
	CmsAdvertisement addAdvertisement(AdvertisementDTO dto);
	
	/**
	 * 修改广告数据
	 * 
	 * @param dto 广告数据DTO
	 * @return CmsAdvertisement
	 */
	CmsAdvertisement saveAdvertisement(AdvertisementDTO dto);
	
	/**
	 * 删除广告数据
	 * 
	 * @param advertisementIds 广告ID列表
	 */
	void deleteAdvertisement(List<Long> advertisementIds);

	/**
	 * 获取广告类型
	 * 
	 * @param typeId 广告类型唯一标识
	 * @return 广告类型实例
	 */
	IAdvertisementType getAdvertisementType(String typeId);

	/**
	 * 广告类型列表
	 * 
	 * @return 广告类型实例列表
	 */
	List<IAdvertisementType> getAdvertisementTypeList();

	/**
	 * 启用广告
	 * 
	 * @param advertisementIds 广告ID列表
	 */
	void enableAdvertisement(List<Long> advertisementIds, String operator);

	/**
	 * 停用广告
	 * 
	 * @param advertisementIds 广告ID列表
	 */
	void disableAdvertisement(List<Long> advertisementIds, String operator);

	/**
	 * 获取广告点击统计地址
	 *
	 * @param adv 广告数据
	 * @param publishPipeCode 发布通道编码
	 * @return 广告点击统计地址
	 */
    String getAdvertisementStatLink(CmsAdvertisement adv, String publishPipeCode);
}
