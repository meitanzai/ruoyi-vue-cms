package com.chestnut.member.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chestnut.common.domain.R;
import com.chestnut.common.exception.CommonErrorCode;
import com.chestnut.common.i18n.I18nUtils;
import com.chestnut.common.log.annotation.Log;
import com.chestnut.common.log.enums.BusinessType;
import com.chestnut.common.security.anno.Priv;
import com.chestnut.common.security.web.BaseRestController;
import com.chestnut.common.utils.Assert;
import com.chestnut.common.utils.StringUtils;
import com.chestnut.member.domain.MemberLevelConfig;
import com.chestnut.member.domain.dto.LevelConfigDTO;
import com.chestnut.member.domain.vo.LevelTypeVO;
import com.chestnut.member.level.ILevelType;
import com.chestnut.member.permission.MemberPriv;
import com.chestnut.member.service.IMemberLevelConfigService;
import com.chestnut.system.security.AdminUserType;
import com.chestnut.system.security.StpAdminUtil;
import com.chestnut.system.validator.LongId;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@Priv(type = AdminUserType.TYPE, value = MemberPriv.MemberLevel)
@RequiredArgsConstructor
@RestController
@RequestMapping("/member/levelConfig")
public class MemberLevelConfigController extends BaseRestController {

	private final IMemberLevelConfigService memberLevelConfigService;

	@GetMapping
	public R<?> getPageList(@RequestParam(value = "levelType", required = false) String levelType,
			@RequestParam(value = "level", required = false) Integer level) {
		PageRequest pr = this.getPageRequest();
		Page<MemberLevelConfig> page = this.memberLevelConfigService.lambdaQuery()
				.eq(StringUtils.isNotEmpty(levelType), MemberLevelConfig::getLevelType, levelType)
				.eq(Objects.nonNull(level), MemberLevelConfig::getLevel, level)
				.page(new Page<>(pr.getPageNumber(), pr.getPageSize(), true));
		page.getRecords().forEach(conf -> {
			ILevelType lt = this.memberLevelConfigService.getLevelType(conf.getLevelType());
			conf.setLevelTypeName(I18nUtils.get(lt.getName()));
		});
		return this.bindDataTable(page);
	}

	@GetMapping("/{configId}")
	public R<?> getLevelConfigDetail(@PathVariable("configId") @LongId Long configId) {
		MemberLevelConfig lvConfig = this.memberLevelConfigService.getById(configId);
		Assert.notNull(lvConfig, () -> CommonErrorCode.DATA_NOT_FOUND_BY_ID.exception("id", configId));
		return R.ok(lvConfig);
	}

	@GetMapping("/types")
	public R<?> getLevelTypes() {
		List<LevelTypeVO> levelTypes = this.memberLevelConfigService.getLevelTypes().values().stream()
				.map(lt -> new LevelTypeVO(lt.getId(), I18nUtils.get(lt.getName()))).toList();
		return R.ok(levelTypes);
	}

	@Log(title = "新增会员等级配置", businessType = BusinessType.INSERT)
	@PostMapping
	public R<?> addMemberConfig(@RequestBody @Validated LevelConfigDTO dto) {
		dto.setOperator(StpAdminUtil.getLoginUser());
		this.memberLevelConfigService.addLevelConfig(dto);
		return R.ok();
	}

	@Log(title = "编辑会员等级配置", businessType = BusinessType.UPDATE)
	@PutMapping
	public R<?> updateMemberConfig(@RequestBody @Validated LevelConfigDTO dto) {
		dto.setOperator(StpAdminUtil.getLoginUser());
		this.memberLevelConfigService.updateLevelConfig(dto);
		return R.ok();
	}

	@Log(title = "删除会员等级配置", businessType = BusinessType.DELETE)
	@DeleteMapping
	public R<?> deleteConfig(@RequestBody @NotEmpty List<Long> configIds) {
		this.memberLevelConfigService.deleteLevelConfig(configIds);
		return R.ok();
	}
}