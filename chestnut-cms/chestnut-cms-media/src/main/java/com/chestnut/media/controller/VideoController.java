package com.chestnut.media.controller;

import com.chestnut.common.domain.R;
import com.chestnut.common.security.anno.Priv;
import com.chestnut.common.security.web.BaseRestController;
import com.chestnut.contentcore.core.InternalURL;
import com.chestnut.contentcore.domain.CmsResource;
import com.chestnut.contentcore.domain.CmsSite;
import com.chestnut.contentcore.service.ISiteService;
import com.chestnut.contentcore.util.InternalUrlUtils;
import com.chestnut.media.domain.dto.VideoScreenshotDTO;
import com.chestnut.media.service.IVideoService;
import com.chestnut.system.security.AdminUserType;
import com.chestnut.system.security.StpAdminUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ws.schild.jave.EncoderException;

import java.io.IOException;
import java.util.Objects;

/**
 * <p>
 * 视频内容前端控制器
 * </p>
 *
 * @author 兮玥
 * @email 190785909@qq.com
 */
@RestController
@RequestMapping("/cms/video")
@RequiredArgsConstructor
public class VideoController extends BaseRestController {

	private final ISiteService siteService;

	private final IVideoService videoService;

	@Priv(type = AdminUserType.TYPE)
	@PostMapping("/screenshot")
	public R<?> screenshot(@RequestBody  @Validated VideoScreenshotDTO dto, HttpServletRequest request)
			throws EncoderException, IOException {
		CmsSite site = this.siteService.getCurrentSite(request);
		InternalURL internalURL = InternalUrlUtils.parseInternalUrl(dto.getPath());
		if (Objects.nonNull(internalURL)) {
			dto.setPath(internalURL.getPath());
		}
		CmsResource cmsResource = this.videoService.videoScreenshot(site, dto.getPath(),
				dto.getTimestamp(), StpAdminUtil.getLoginUser());
		return R.ok(cmsResource);
	}
}
