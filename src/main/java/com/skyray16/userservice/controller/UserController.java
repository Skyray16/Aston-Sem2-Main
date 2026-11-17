package com.skyray16.userservice.controller;

import com.skyray16.userservice.dto.UserRequestDto;
import com.skyray16.userservice.dto.UserResponseDto;
import com.skyray16.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Создать пользователя", description = "Возвращает созданного пользователя с ссылками")
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto created = userService.createUser(dto);
        EntityModel<UserResponseDto> model = addLinks(created);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public ResponseEntity<EntityModel<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        EntityModel<UserResponseDto> model = addLinks(user);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDto>>> getAllUsers() {
        List<EntityModel<UserResponseDto>> users = userService.getAllUsers().stream()
                .map(user -> addLinks(user))
                .toList();

        CollectionModel<EntityModel<UserResponseDto>> collectionModel = CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя")
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto dto) {
        UserResponseDto updated = userService.updateUser(id, dto);
        EntityModel<UserResponseDto> model = addLinks(updated);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<UserResponseDto> addLinks(UserResponseDto user) {
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"),
                linkTo(methodOn(UserController.class).deleteUser(user.id())).withRel("delete")
        );
    }
}