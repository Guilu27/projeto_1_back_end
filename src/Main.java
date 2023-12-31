import dao.ClienteMapDAO;
import dao.IClienteDAO;
import domain.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Main {
    private static IClienteDAO iClienteDAO;
    private static final JFrame frame = new JFrame("Cadastrar");
    private static final JPanel panel = new JPanel();
    private static final JLabel nomeLabel = new JLabel("Nome: ");
    private static final JTextField nomeTextField = new JTextField();
    private static final JLabel cpfLabel = new JLabel("CPF: ");
    private static final JTextField cpfTextField = new JTextField();
    private static final JLabel telefoneLabel = new JLabel("Telefone: ");
    private static final JTextField telefoneTextField = new JTextField();
    private static final JLabel enderecoLabel = new JLabel("Endereço: ");
    private static final JTextField enderecoTextField = new JTextField();
    private static final JLabel numeroLabel = new JLabel("Número: ");
    private static final JTextField numeroTextField = new JTextField();
    private static final JLabel cidadelabel = new JLabel("Cidade:");
    private static final JTextField cidadeTextField = new JTextField();
    private static final JLabel estadoLabel = new JLabel("Estado:");
    private static final JTextField estadoTextField = new JTextField();
    private static final JButton botaoLimpar = new JButton("Limpar");
    private static final JButton botaoExcluir = new JButton("Excluir");
    private static final JButton botaoAtualizar = new JButton("Atualizar");
    private static final JButton botaoSalvar = new JButton("Salvar");
    private static final DefaultTableModel tabelaModelo = new DefaultTableModel();
    private static JTable tabelaClientes;

    public static void main(String[] args) {
        iClienteDAO = new ClienteMapDAO();

        configureUI();
        addComponents();
        addActions();
        displayFrame();
    }

    private static boolean validarCampos() {
        String nome = nomeTextField.getText();
        String cpf = cpfTextField.getText();
        String telefone = telefoneTextField.getText();
        String endereco = enderecoTextField.getText();
        String numero = numeroTextField.getText();
        String cidade = cidadeTextField.getText();
        String estado = estadoTextField.getText();

        return isCamposValidos(nome, endereco, numero, cidade, estado) && isCamposNumeros(cpf, telefone, numero);
    }

    private static Cliente criarCliente() {
        String nome = nomeTextField.getText();
        String cpf = cpfTextField.getText();
        String telefone = telefoneTextField.getText();
        String endereco = enderecoTextField.getText();
        String numero = numeroTextField.getText();
        String cidade = cidadeTextField.getText();
        String estado = estadoTextField.getText();

        return new Cliente(nome, Long.parseLong(cpf), Long.parseLong(telefone), endereco, Integer.parseInt(numero), cidade, estado);
    }

    //Configurar interface do usuario
    private static void configureUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(700, 606);
        panel.setLayout(null);
    }

    private static void addComponents() {
        criarMenuBar();
        mudarFonteLabel();
        adicionarComponentesInputs();
        adicionarBotoesPanel();
        adicionarTabela();
    }

    private static void addActions() {
        actionBotaoLimpar();
        actionBotaoExcluir();
        actionBotaoAtualizar();
        actionBotaoSalvar();
        selecionarLinhaTabela();
    }

    private static void displayFrame() {
        frame.add(panel);
        frame.setVisible(true);
    }

    //Salvar clientes
    private static void actionBotaoSalvar() {
        botaoSalvar.addActionListener(e -> salvarDadosTabela());
    }

    private static void salvarDadosTabela() {
        if (validarCampos()) {
            Cliente cliente = criarCliente();
            Boolean isCadastrado = iClienteDAO.cadastrar(cliente);

            if (isCadastrado) {
                tabelaModelo.addRow(new Object[]{cliente.getNome(), cliente.getCpf().toString(), cliente.getTel(), cliente.getEnd()});
                exibirMensagemCadastroSucesso();
            } else {
                exibirMensagemErroCpfDuplicado();
                return;
            }
            limparCampos();
        }
    }

    private static void exibirMensagemCadastroSucesso() {
        JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void exibirMensagemErroCpfDuplicado() {
        JOptionPane.showMessageDialog(null, "CPF do Cliente já está cadastrado", "Erro", JOptionPane.INFORMATION_MESSAGE);
    }

    //Atulizar cliente
    private static void actionBotaoAtualizar() {
        botaoAtualizar.addActionListener(e -> atualizarCliente());
    }

    private static void atualizarCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();

        if (linhaSelecionada != -1) {
            if (validarCampos()) {
                if (confirmarAtualizacao()) {
                    Long cpf = Long.parseLong((String) tabelaClientes.getValueAt(linhaSelecionada, 1));
                    iClienteDAO.excluir(cpf);

                    atualizarTabela(linhaSelecionada);
                    Cliente cliente = criarCliente();
                    iClienteDAO.cadastrar(cliente);

                    JOptionPane.showMessageDialog(null,
                            "Cliente atualizado com sucesso ",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    limparCampos();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum cliente selecionado", "Erro", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static boolean confirmarAtualizacao() {
        int result = JOptionPane.showConfirmDialog(null, "Deseja realmente atualizar esse cliente?", "Cuidado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    private static void atualizarTabela(int linha) {
        tabelaModelo.setValueAt(nomeTextField.getText(), linha, 0);
        tabelaModelo.setValueAt(cpfTextField.getText(), linha, 1);
        tabelaModelo.setValueAt(telefoneTextField.getText(), linha, 2);
        tabelaModelo.setValueAt(enderecoTextField.getText(), linha, 3);
    }


    private static void actionBotaoExcluir() {
        botaoExcluir.addActionListener(e -> excluirCliente());
    }

    private static void excluirCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();

        if (linhaSelecionada != -1) {

            int result = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esse cliente?", "Cuidado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_NO_OPTION) {
                Long cpf = Long.parseLong((String) tabelaClientes.getValueAt(linhaSelecionada, 1));
                iClienteDAO.excluir(cpf);
                tabelaModelo.removeRow(linhaSelecionada);
                limparCampos();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum cliente selecionado", "Erro", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void selecionarLinhaTabela() {
        ListSelectionModel selacaoTabela = tabelaClientes.getSelectionModel();

        selacaoTabela.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int linhaSelecionada = tabelaClientes.getSelectedRow();

                if (linhaSelecionada != -1) {
                    Long cpf = Long.parseLong((String) tabelaClientes.getValueAt(linhaSelecionada, 1));

                    Cliente cliente = iClienteDAO.consultar(cpf);

                    mudarValorTextFields(cliente);
                }
            }
        });
    }

    private static void mudarValorTextFields(Cliente cliente) {
        nomeTextField.setText(cliente.getNome());
        cpfTextField.setText(cliente.getCpf().toString());
        telefoneTextField.setText(cliente.getTel().toString());
        enderecoTextField.setText(cliente.getEnd());
        numeroTextField.setText(cliente.getNumero().toString());
        cidadeTextField.setText(cliente.getCidade());
        estadoTextField.setText(cliente.getEstado());
    }

    private static void actionBotaoLimpar() {
        botaoLimpar.addActionListener(e -> limparCampos());
    }


    private static boolean isCamposNumeros(String... campos) {
        for (String campo : campos) {
            try {
                Long.parseLong(campo);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Os campos CPF, Telefone e Número aceitam somente números",
                        "Erro", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private static boolean isCamposValidos(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Há campos a serem preenchidos",
                        "Erro", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private static void limparCampos() {
        nomeTextField.setText("");
        cpfTextField.setText("");
        telefoneTextField.setText("");
        enderecoTextField.setText("");
        numeroTextField.setText("");
        cidadeTextField.setText("");
        estadoTextField.setText("");
    }

    private static void adicionarTabela() {
        tabelaModelo.addColumn("Nome");
        tabelaModelo.addColumn("CPF");
        tabelaModelo.addColumn("Telefone");
        tabelaModelo.addColumn("Endereço");

        tabelaClientes = new JTable(tabelaModelo);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);

        scrollPane.setBounds(264, 16, 388, 512);

        frame.add(scrollPane);
    }

    private static void adicionarBotoesPanel() {
        int y = estadoTextField.getY() + 40;

        botaoLimpar.setBounds(32, y, 92, 24);
        panel.add(botaoLimpar);

        botaoExcluir.setBounds(140, y, 92, 24);
        panel.add(botaoExcluir);

        y += 40;

        botaoAtualizar.setBounds(32, y, 92, 24);
        panel.add(botaoAtualizar);

        botaoSalvar.setBounds(140, y, 92, 24);
        panel.add(botaoSalvar);
    }

    private static void adicionarComponentesInputs() {
        adicionarComponentes(nomeLabel, nomeTextField, 16);
        int y = nomeTextField.getY() + 40;
        adicionarComponentes(cpfLabel, cpfTextField, y);
        y = cpfTextField.getY() + 40;
        adicionarComponentes(telefoneLabel, telefoneTextField, y);
        y = telefoneTextField.getY() + 40;
        adicionarComponentes(enderecoLabel, enderecoTextField, y);
        y = enderecoTextField.getY() + 40;
        adicionarComponentes(numeroLabel, numeroTextField, y);
        y = numeroTextField.getY() + 40;
        adicionarComponentes(cidadelabel, cidadeTextField, y);
        y = cidadeTextField.getY() + 40;
        adicionarComponentes(estadoLabel, estadoTextField, y);
    }

    private static void mudarFonteLabel() {
        Font novaFonte = nomeLabel.getFont().deriveFont(20f);

        nomeLabel.setFont(novaFonte);
        cpfLabel.setFont(novaFonte);
        telefoneLabel.setFont(novaFonte);
        enderecoLabel.setFont(novaFonte);
        numeroLabel.setFont(novaFonte);
        cidadelabel.setFont(novaFonte);
        enderecoLabel.setFont(novaFonte);
        estadoLabel.setFont(novaFonte);
    }

    private static void adicionarComponentes(JLabel label, JTextField textField, int y) {
        label.setBounds(32, y, 200, 24);
        textField.setBounds(32, y + 24, 200, 24);
        panel.add(label);
        panel.add(textField);
    }

    private static void criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu opcoesMenu = new JMenu("Opções");

        JMenuItem sairItem = new JMenuItem("Sair");

        sairItem.addActionListener(Main::menuSairActionPerformed);

        opcoesMenu.add(sairItem);

        menuBar.add(opcoesMenu);

        frame.setJMenuBar(menuBar);
    }

    private static void menuSairActionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog(null, "Deseja sair da aplicacao?", "Sair", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_NO_OPTION) {
            System.exit(0);
        }
    }
}