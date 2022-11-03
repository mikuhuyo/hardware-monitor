package com.example.backend.controller.chain;

import com.example.backend.service.chain.IBoardService;
import com.example.chain.dto.BoardDTO;
import com.example.chain.vo.BoardVO;
import com.example.common.domain.RestResponse;
import com.example.common.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
@RequestMapping("/api")
public class BoardController {
    @Autowired
    private IBoardService boardService;

    @GetMapping("/board/my")
    public List<BoardDTO> getMyBoard(HttpServletRequest request) {
        Long userId = TokenUtil.verifyToken(request.getHeader("Authorization"));

        return boardService.getMyBoard(userId);
    }

    @GetMapping("/board/system")
    public List<BoardDTO> getSystemBoard() {
        return boardService.getSystemBoard();
    }

    @PutMapping("/board/status")
    public RestResponse<String> updateBoardStatus(@RequestBody List<Map<String, String>> data) {
        boardService.updateBoardStatus(data);

        return RestResponse.success();
    }

    @PutMapping("/board")
    public RestResponse<String> updateBoard(@RequestBody BoardVO boardVO) {
        boardService.updateBoard(boardVO);

        return RestResponse.success();
    }

    @DeleteMapping("/board/{id}")
    public RestResponse<String> deleteBoard(@PathVariable("id") Long id) {
        boardService.deleteBoardById(id);

        return RestResponse.success();
    }

    @PostMapping("/board")
    public RestResponse<String> createBoard(@RequestBody BoardVO boardVO) {
        boardService.createBoard(boardVO);

        return RestResponse.success();
    }
}
