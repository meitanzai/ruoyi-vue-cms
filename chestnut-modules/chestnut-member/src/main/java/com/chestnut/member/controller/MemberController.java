package com.chestnut.member.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
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
import com.chestnut.common.log.annotation.Log;
import com.chestnut.common.log.enums.BusinessType;
import com.chestnut.common.security.anno.Priv;
import com.chestnut.common.security.web.BaseRestController;
import com.chestnut.common.utils.Assert;
import com.chestnut.common.utils.IdUtils;
import com.chestnut.common.utils.StringUtils;
import com.chestnut.member.domain.Member;
import com.chestnut.member.domain.dto.MemberDTO;
import com.chestnut.member.domain.vo.MemberListVO;
import com.chestnut.member.fixed.config.EncryptMemberPhoneAndEmail;
import com.chestnut.member.permission.MemberPriv;
import com.chestnut.member.service.IMemberService;
import com.chestnut.system.security.AdminUserType;
import com.chestnut.system.security.StpAdminUtil;
import com.chestnut.system.validator.LongId;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@Priv(type = AdminUserType.TYPE, value = MemberPriv.MemberList)
@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController extends BaseRestController {

	private final IMemberService memberService;

	@GetMapping
	public R<?> getPageList(@RequestParam(required = false) String userName,
			@RequestParam(required = false) String nickName, @RequestParam(required = false) String email,
			@RequestParam(required = false) String phoneNumber, @RequestParam(required = false) String status) {
		PageRequest pr = this.getPageRequest();
		Page<Member> page = this.memberService.lambdaQuery()
				.like(StringUtils.isNotEmpty(userName), Member::getUserName, userName)
				.like(StringUtils.isNotEmpty(nickName), Member::getNickName, nickName)
				.like(StringUtils.isNotEmpty(email), Member::getEmail, email)
				.like(StringUtils.isNotEmpty(phoneNumber), Member::getPhoneNumber, phoneNumber)
				.eq(StringUtils.isNotEmpty(status), Member::getStatus, status)
				.page(new Page<>(pr.getPageNumber(), pr.getPageSize(), true));
		List<MemberListVO> list = page.getRecords().stream().map(m -> {
			MemberListVO vo = new MemberListVO();
			BeanUtils.copyProperties(m, vo);
			if (EncryptMemberPhoneAndEmail.getValue()) {
				vo.setEmail(EncryptMemberPhoneAndEmail.encryptEmail(vo.getEmail()));
				vo.setPhoneNumber(EncryptMemberPhoneAndEmail.encryptPhone(vo.getPhoneNumber()));
			}
			return vo;
		}).toList();
		return this.bindDataTable(list, page.getTotal());
	}

	@GetMapping("/{memberId}")
	public R<?> getMemberDetail(@PathVariable @LongId Long memberId) {
		Member member = this.memberService.getById(memberId);
		Assert.notNull(member, () -> CommonErrorCode.DATA_NOT_FOUND_BY_ID.exception("memberId", memberId));
		MemberListVO vo = new MemberListVO();
		BeanUtils.copyProperties(member, vo);
		return R.ok(vo);
	}

	@Log(title = "新增会员", businessType = BusinessType.INSERT, isSaveRequestData = false)
	@PostMapping
	public R<?> addMember(@RequestBody @Validated MemberDTO dto) {
		dto.setOperator(StpAdminUtil.getLoginUser());
		this.memberService.addMember(dto);
		return R.ok();
	}

	@Log(title = "编辑会员", businessType = BusinessType.UPDATE)
	@PutMapping
	public R<?> updateMember(@RequestBody @Validated MemberDTO dto) {
		dto.setOperator(StpAdminUtil.getLoginUser());
		this.memberService.updateMember(dto);
		return R.ok();
	}

	@Log(title = "删除会员", businessType = BusinessType.DELETE)
	@DeleteMapping
	public R<?> delete(@RequestBody @NotEmpty List<Long> memberIds) {
		IdUtils.validate(memberIds);
		this.memberService.deleteMembers(memberIds);
		return R.ok();
	}

	@Log(title = "重置会员密码", businessType = BusinessType.UPDATE, isSaveRequestData = false)
	@PutMapping("/resetPassword")
	public R<?> resetPassword(@RequestBody @Validated MemberDTO dto) {
		dto.setOperator(StpAdminUtil.getLoginUser());
		this.memberService.resetPwd(dto);
		return R.ok();
	}
}