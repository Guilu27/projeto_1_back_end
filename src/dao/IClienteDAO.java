package dao;

import domain.Cliente;

import java.util.Collection;

public interface IClienteDAO {
    public Boolean cadastrar(Cliente cliente);

    public Cliente excluir(Long cpf);

    Cliente consultar(Long cpf);

    public Collection<Cliente> buscarTodos();
}
