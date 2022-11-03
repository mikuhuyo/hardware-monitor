package com.example.backend.controller.chain;

import com.example.backend.service.chain.IQuotaService;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.query.QuotaQuery;
import com.example.chain.vo.QuotaVO;
import com.example.common.domain.RestPageResult;
import com.example.common.domain.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
@RequestMapping("/api")
public class QuotaController {
    @Autowired
    private IQuotaService quotaService;

    @GetMapping("/quota/search/integer")
    public RestResponse<List<QuotaDTO>> findQuotaByInteger() {
        return RestResponse.success(quotaService.findQuotaByNumber());
    }

    @PostMapping("/quota/search/integer")
    public RestPageResult<List<QuotaDTO>> searchQuotaInteger(@RequestBody QuotaQuery quotaQuery) {
        return quotaService.searchQuotaByNumber(quotaQuery);
    }

    @PostMapping("/quota/search")
    public RestPageResult<List<QuotaDTO>> searchQuota(@RequestBody QuotaQuery quotaQuery) {
        return quotaService.searchQuota(quotaQuery);
    }

    @GetMapping("/quota/{id}")
    public QuotaDTO findQuotaById(@PathVariable("id") Long id) {
        return quotaService.findQuotaById(id);
    }

    @PutMapping("/quota")
    public boolean updateQuota(@RequestBody QuotaVO quotaVO) {
        return quotaService.updateQuotaById(quotaVO);
    }

    @DeleteMapping("/quota/{id}")
    public boolean removeQuota(@PathVariable("id") Long id) {
        return quotaService.removeQuotaById(id);
    }

    @PostMapping("/quota")
    public boolean createQuota(@RequestBody QuotaVO quotaVO) {
        return quotaService.createQuota(quotaVO);
    }
}
