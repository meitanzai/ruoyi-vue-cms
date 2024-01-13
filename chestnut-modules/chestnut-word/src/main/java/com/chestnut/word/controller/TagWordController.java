package com.chestnut.word.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chestnut.common.domain.R;
import com.chestnut.common.security.anno.Priv;
import com.chestnut.common.security.web.BaseRestController;
import com.chestnut.common.security.web.PageRequest;
import com.chestnut.common.utils.StringUtils;
import com.chestnut.system.security.AdminUserType;
import com.chestnut.system.security.StpAdminUtil;
import com.chestnut.word.domain.TagWord;
import com.chestnut.word.permission.WordPriv;
import com.chestnut.word.service.ITagWordService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * TAG词前端控制器
 * </p>
 *
 * @author 兮玥
 * @email 190785909@qq.com
 */
@Priv(type = AdminUserType.TYPE, value = WordPriv.View)
@RequiredArgsConstructor
@RestController
@RequestMapping("/word/tagword")
public class TagWordController extends BaseRestController {

	private final ITagWordService tagWordService;

	@GetMapping
	public R<?> getPageList(@RequestParam("groupId") @Min(1) Long groupId,
			@RequestParam(value = "query", required = false) String query) {
		PageRequest pr = this.getPageRequest();
		Page<TagWord> page = this.tagWordService.lambdaQuery().eq(TagWord::getGroupId, groupId)
				.like(StringUtils.isNotEmpty(query), TagWord::getWord, query)
				.orderByAsc(TagWord::getSortFlag)
				.page(new Page<>(pr.getPageNumber(), pr.getPageSize(), true));
		return this.bindDataTable(page);
	}

	@PostMapping
	public R<?> add(@RequestBody @Validated TagWord tagWord) {
		tagWord.createBy(StpAdminUtil.getLoginUser().getUsername());
		this.tagWordService.addTagWord(tagWord);
		return R.ok();
	}

	@PutMapping
	public R<?> edit(@RequestBody @Validated TagWord tagWord) {
		tagWord.updateBy(StpAdminUtil.getLoginUser().getUsername());
		this.tagWordService.editTagWord(tagWord);
		return R.ok();
	}

	@DeleteMapping
	public R<?> remove(@RequestBody @NotEmpty List<Long> tagWordIds) {
		this.tagWordService.deleteTagWords(tagWordIds);
		return R.ok();
	}
}
