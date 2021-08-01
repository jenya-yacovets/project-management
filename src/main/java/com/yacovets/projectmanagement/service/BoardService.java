package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.Board;
import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.BoardCreateModel;
import com.yacovets.projectmanagement.repository.BoardRepository;
import com.yacovets.projectmanagement.repository.ProjectRepository;
import com.yacovets.projectmanagement.service.exception.BoardNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotAccessServiceException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public BoardService(BoardRepository boardRepository, ProjectRepository projectRepository, ModelMapper modelMapper) {
        this.boardRepository = boardRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    public Board createBoard(BoardCreateModel boardModel, String projectAlias, User user) throws UserNotAccessServiceException {
        if (!projectRepository.existsByAliasAndTeams(projectAlias, user)) {
            throw new UserNotAccessServiceException();
        }

        Board boardMapped = modelMapper.map(boardModel, Board.class);
        boardMapped.setProject(new Project(projectAlias));
        boardMapped.setCreator(user);

        Board saveBoard = boardRepository.save(boardMapped);

        log.info("Create new board: {}", saveBoard);
        return saveBoard;
    }

    public Optional<Board> getBoard(long id, User user) {
        return boardRepository.findByIdAndProjectTeams(id, user);
    }

    public List<Board> getAllBoard(User user) {
        return boardRepository.findAllByProjectTeams(user);
    }

    public Board deleteBoard(long id, User user) throws BoardNotFoundServiceException {
        Board board = boardRepository.findByIdAndProjectTeams(id, user).orElseThrow(BoardNotFoundServiceException::new);
        boardRepository.delete(board);
        log.info("Success delete board: {}", board);
        return board;
    }
}
