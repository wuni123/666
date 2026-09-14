package com.rural.agri.service;

import com.rural.agri.dto.LoginDTO;
import com.rural.agri.dto.RegisterDTO;
import com.rural.agri.vo.LoginVO;

public interface UserService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);
}
