package com.yacovets.projectmanagement.controller;

import com.yacovets.projectmanagement.controller.exception.DataNotFoundControllerException;
import com.yacovets.projectmanagement.controller.exception.UserNotAccessControllerException;
import com.yacovets.projectmanagement.entity.Board;
import com.yacovets.projectmanagement.entity.Project;
import com.yacovets.projectmanagement.entity.User;
import com.yacovets.projectmanagement.model.BoardCreateModel;
import com.yacovets.projectmanagement.service.BoardService;
import com.yacovets.projectmanagement.service.ProjectService;
import com.yacovets.projectmanagement.service.exception.BoardNotFoundServiceException;
import com.yacovets.projectmanagement.service.exception.UserNotAccessServiceException;
import com.yacovets.projectmanagement.util.CollectionUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/board")
public class BoardController {
    private final ProjectService projectService;
    private final BoardService boardService;
    private final CollectionUtil collectionUtil;

    public BoardController(ProjectService projectService, BoardService boardService, CollectionUtil collectionUtil) {
        this.projectService = projectService;
        this.boardService = boardService;
        this.collectionUtil = collectionUtil;
    }

    @GetMapping("/create")
    public ModelAndView createBoardGetListProjectView(@CurrentSecurityContext(expression = "authentication")Authentication authentication){
        User user = (User) authentication.getPrincipal();
        List<Project> allProject = projectService.getAllProject(user);
        return new ModelAndView("boardProjectCreate", "projectList", allProject);
    }

    @GetMapping("/create/{projectAlias}")
    public ModelAndView createBoardView(@PathVariable String projectAlias, @CurrentSecurityContext(expression = "authentication")Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        Optional<Project> project = projectService.getProject(projectAlias, user);
        if (project.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("boardCreate", "boardModel", new BoardCreateModel());
            modelAndView.addObject("project", project.get());
            return modelAndView;
        }
        throw new DataNotFoundControllerException("Project not found");
    }

    @PostMapping("/create/{projectAlias}")
    public ModelAndView createBoardHandler(@Valid @ModelAttribute("boardModel") BoardCreateModel boardModel, BindingResult result, @PathVariable String projectAlias, @CurrentSecurityContext(expression = "authentication")Authentication authentication) throws UserNotAccessControllerException {
        if (result.hasErrors()) {
            return new ModelAndView("boardCreate", "boardModel", result.getModel());
        }

        User user = (User) authentication.getPrincipal();

        try {
            Board board = boardService.createBoard(boardModel, projectAlias, user);
            return new ModelAndView(String.format("redirect:/board/%d", board.getId()));
        } catch (UserNotAccessServiceException e) {
            throw new UserNotAccessControllerException();
        }
    }

    @GetMapping("/list")
    public ModelAndView listBoardView(@CurrentSecurityContext(expression = "authentication") Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return new ModelAndView("boardList", "boardList", boardService.getAllBoard(user));
    }

    @GetMapping("/{id}")
    public ModelAndView getBoardView(@PathVariable long id, @CurrentSecurityContext(expression = "authentication") Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        Optional<Board> board = boardService.getBoard(id, user);

        if (board.isPresent()) {
            ModelAndView model = new ModelAndView("board", "board", board.get());
            model.addObject("taskList", collectionUtil.splitCollection(new ArrayList<>(board.get().getTasks()), 4));
            return model;
        }
        throw new DataNotFoundControllerException();
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteBoard(@PathVariable long id, @CurrentSecurityContext(expression = "authentication") Authentication authentication) throws DataNotFoundControllerException {
        User user = (User) authentication.getPrincipal();
        try {
            Board board = boardService.deleteBoard(id, user);
            return new ModelAndView(String.format("redirect:/project/%s", board.getProject().getAlias()), "message", "The board was successfully deleted");
        } catch (BoardNotFoundServiceException e) {
            throw new DataNotFoundControllerException("Board not found");
        }
    }
}
