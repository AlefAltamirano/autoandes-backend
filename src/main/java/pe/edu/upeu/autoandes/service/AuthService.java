package pe.edu.upeu.autoandes.service;

import pe.edu.upeu.autoandes.dto.LoginRequestDTO;
import pe.edu.upeu.autoandes.dto.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
}
