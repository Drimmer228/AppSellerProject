package com.example.springmodels;

import com.example.springmodels.controllers.UserController;
import com.example.springmodels.models.modelUser;
import com.example.springmodels.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserControllerTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @Test
    public void testUserView() throws Exception {
        // Тест на проверку корректного отображения пользователей
        Model model = mock(Model.class);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        String viewName = userController.userView(model);

        // Проверка вызова метода findAll() у userRepository и добавления атрибута в модель
        verify(userRepository, times(1)).findAll();
        verify(model, times(1)).addAttribute(eq("user_list"), any());
        assert viewName.equals("admin/index");
    }

    @Test
    public void testDetailViewByIdExists() throws Exception {
        // Тест на проверку просмотра деталей существующего пользователя по ID
        Model model = mock(Model.class);
        Long userId = 1L;
        modelUser user = new modelUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String viewName = userController.detailView(userId, model);

        // Проверка вызова метода findById() у userRepository и добавления атрибута в модель
        verify(userRepository, times(1)).findById(userId);
        verify(model, times(1)).addAttribute(eq("user_object"), any());
        assert viewName.equals("admin/info");
    }

    @Test
    public void testUpdateUser() throws Exception {
        // Тест на проверку обновления данных пользователя
        Long userId = 1L;
        String username = "testUser";
        String[] roles = {"ADMIN", "USER"};
        modelUser user = new modelUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String redirectUrl = userController.update_user(username, roles, userId);

        // Проверка вызова методов findById() и save() у userRepository
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any());
        assert redirectUrl.equals("redirect:/admin/{id}");
    }
}
