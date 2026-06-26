package br.unitins.tp1.sga.model;

import jakarta.persistence.Entity;

/**
 * Administrador do sistema — entidade própria (não é mais um Cuteleiro com
 * flag). Apenas administradores podem criar outros administradores
 * (ver AdministradorResource).
 *
 * Herda de Usuario: nome, email, telefone, login, senha (login e-commerce,
 * geralmente não usado por admins), senhaAdministrativa (segunda senha,
 * usada em /administrativo/v1/auth/login) e tipoUsuario (= ADMINISTRADOR).
 */
@Entity
public class Administrador extends Usuario {
}
