package com.example.backend.service.chain;

import com.example.chain.dto.BoardDTO;
import com.example.chain.vo.BoardVO;

import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IBoardService {
    List<BoardDTO> getMyBoard(Long uid);

    List<BoardDTO> getSystemBoard();

    void updateBoardStatus(List<Map<String, String>> statusMap);

    void updateBoard(BoardVO boardVO);

    void deleteBoardById(Long id);

    void createBoard(BoardVO boardVO);
}
