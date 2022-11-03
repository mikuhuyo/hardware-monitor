package com.example.backend.service.chain.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.mapper.chain.BoardMapper;
import com.example.backend.service.chain.IBoardService;
import com.example.chain.dto.BoardDTO;
import com.example.chain.pojo.Board;
import com.example.chain.vo.BoardVO;
import com.example.common.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IBoardServiceImpl implements IBoardService {
    @Autowired
    private BoardMapper boardMapper;

    @Override
    public List<BoardDTO> getMyBoard(Long uid) {
        List<Board> boards = boardMapper.selectList(new QueryWrapper<Board>().lambda().eq(Board::getIsSystem, "0").eq(Board::getIsDisable, "0").eq(Board::getUserId, uid));

        return entityList2dtoList(boards);
    }

    @Override
    public List<BoardDTO> getSystemBoard() {
        List<Board> boards = boardMapper.selectList(new QueryWrapper<Board>().lambda().eq(Board::getIsSystem, "1").eq(Board::getIsDisable, "0"));

        return entityList2dtoList(boards);
    }

    private List<BoardDTO> entityList2dtoList(List<Board> boards) {
        if (boards.isEmpty()) {
            return null;
        }

        List<BoardDTO> boardDTOList = Lists.newArrayList();
        for (Board board : boards) {
            BoardDTO boardDTO = new BoardDTO();
            BeanUtils.copyProperties(board, boardDTO);

            boardDTOList.add(boardDTO);
        }

        return boardDTOList;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void updateBoardStatus(List<Map<String, String>> statusMap) {
        for (Map<String, String> stringStringMap : statusMap) {
            String boardId = stringStringMap.get("boardId");
            String disable = stringStringMap.get("disable");

            Board board = new Board();
            board.setId(Long.valueOf(boardId));
            board.setIsDisable("true".equals(disable) ? "1" : "0");

            boardMapper.updateById(board);
        }
    }

    @Override
    public void updateBoard(BoardVO boardVO) {
        Board board = new Board();
        BeanUtils.copyProperties(boardVO, board);

        boardMapper.updateById(board);
    }

    @Override
    public void deleteBoardById(Long id) {
        boardMapper.deleteById(id);
    }

    @Override
    public void createBoard(BoardVO boardVO) {
        Board board = new Board();

        BeanUtils.copyProperties(boardVO, board);

        Integer integer = boardMapper.selectCount(new QueryWrapper<Board>().lambda().eq(Board::getBoardName, board.getBoardName()).eq(Board::getUserId, board.getUserId()));
        if (integer == null || integer <= 0) {
            throw new BusinessException("添加失败, 该看板已存在");
        }

        board.setIsDisable("0");
        board.setIsSystem("0");

        boardMapper.insert(board);

    }
}
