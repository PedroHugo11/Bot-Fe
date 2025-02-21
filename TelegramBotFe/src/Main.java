import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class Main {

    public static void main(String[] args) {

        //Criação do objeto bot com as informações de acesso
        TelegramBot bot = TelegramBotAdapter.build("1036167275:AAFf0aVfpYqgkv1_NAxu1psKZXi7YwAczbI");
        //objeto responsável por receber as mensagens
        GetUpdatesResponse updatesResponse;
        //objeto responsável por gerenciar o envio de respostas
        SendResponse sendResponse;
        //objeto responsável por gerenciar o envio de ações do chat
        BaseResponse baseResponse;

        //controle de off-set, isto é, a partir deste ID será lido as mensagens pendentes na fila
        int m = 0;

        Localizacao localizacao = new Localizacao(null, null);
        Categoria categoria = new Categoria(null, null, null);
        Bem bem = new Bem(null, null,null, null, null);

        ArrayList<Bem> controle_bens = new ArrayList<Bem>();
        ArrayList<Localizacao> controle_local_busca = new ArrayList<Localizacao>();
        ArrayList<Categoria> controle_categoria_busca = new ArrayList<Categoria>();
        SortedSet<String> controle_nome_busca = new TreeSet<String>();

        boolean controle_localizacao = false;
        boolean controle_categoria = false;
        boolean controle_bem = false;
        boolean controle_busca = false;
        boolean controle_nome = false;
        boolean controle_descricao = false;
        boolean controle_movimenta = false;
        boolean checa_codigo = false;
        boolean checa_localizacao = false;
        String nome_localizacao = null;
        String nome_categoria = null;
        String codigo = null;
        String nome = null;
        String descricao = null;

        Gerenciador gerencia = new Gerenciador(bot);

        //loop infinito pode ser alterado por algum timer de intervalo curto
        while (true) {

            //executa comando no Telegram para obter as mensagens pendentes a partir de um off-set (limite inicial)
            updatesResponse = bot.execute(new GetUpdates().limit(100).offset(m));
            //lista de mensagens
            List<Update> updates = updatesResponse.updates();
            //análise de cada ação da mensagem
            for (Update update : updates) {

                String iniciaBot = update.message().text();

                if (iniciaBot.equals("Olá")) {
                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "" +
                            "Olá, sou o Bot Fé! Quando desejar a apresentação do menu de opções digite /menu"));
                }

                if (iniciaBot.equals("/menu")) {
                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "" +
                            "########### BOT FÉ - MENU PRINCIPAL ###########\n" +
                            "1. /cadastrar_localizacao\n" +
                            "2. /cadastrar_categoria\n" +
                            "3. /cadastrar_bem\n" +
                            "4. /listar_localizacoes\n" +
                            "5. /listar_categorias\n" +
                            "6. /listar_bens_de_localizacao\n" +
                            "7. /buscar_bem_por_codigo\n" +
                            "8. /buscar_bem_por_nome\n" +
                            "9. /buscar_bem_por_descricao\n" +
                            "10. /movimentar_bem\n" +
                            "11. /gerar_relatorio\n\n" +
                            "# Insira o comando correspondente ao que deseja #"));
                }

                m = update.updateId() + 1;
                System.out.println("Recebendo mensagem:" + update.message().text());
                String respostaMenu = update.message().text();

                //#################### 1 ESCOLHA ########################

                if (respostaMenu.equals("/cadastrar_localizacao") || controle_localizacao) {
                    if(localizacao.getNome() == null) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "##### CADASTRAR LOCALIZAÇÃO #####\n"));
                        m = update.updateId() + 1;

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o nome da localização: "));
                        m = update.updateId() + 1;

                        localizacao.setNome(update.message().text());
                        controle_localizacao = true;
                    }
                    else if (localizacao.getDescricao() == null) {
                        localizacao.setNome(update.message().text());

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite a sua descrição: "));
                        localizacao.setDescricao(update.message().text());
                    }
                    else if (localizacao.getDescricao() != null) {
                        localizacao.setDescricao(update.message().text());
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Localização cadastrada com sucesso!" +
                                "\nNome: " + localizacao.getNome() + "\nDescrição: "+ localizacao.getDescricao()));
                        gerencia.getLocalizacoes().add(localizacao);
                        localizacao = new Localizacao(null, null);
                        controle_localizacao = false;
                    }
                }

                //#################### 2 ESCOLHA ########################
                else if (respostaMenu.equals("/cadastrar_categoria") || controle_categoria) {
                    if(categoria.getCodigo() == null) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "##### CADASTRAR CATEGORIA #####\n"));
                        m = update.updateId() + 1;

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o código da categoria:"));
                        m = update.updateId() + 1;

                        categoria.setCodigo(update.message().text());
                        controle_categoria = true;
                    }
                    else if(categoria.getNome() == null) {
                        categoria.setCodigo(update.message().text());
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o nome da categoria:"));
                        m = update.updateId() + 1;

                        categoria.setNome(update.message().text());
                    }
                    else if (categoria.getDescricao() == null) {
                        categoria.setNome(update.message().text());

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite a descrição da cateogira: "));
                        categoria.setDescricao(update.message().text());
                    }
                    else if (categoria.getDescricao() != null) {
                        categoria.setDescricao(update.message().text());
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Categoria cadastrada com sucesso!\nCódigo: "+ categoria.getCodigo() + "\nNome: " +
                                categoria.getNome() + "\nDescrição: "+ categoria.getDescricao()));
                        gerencia.getCategorias().add(categoria);
                        categoria = new Categoria(null, null, null);
                        controle_categoria = false;
                    }

                }

                //#################### 3 ESCOLHA ########################
                else if (respostaMenu.equals("/cadastrar_bem") || controle_bem) {
                    if(bem.getCodigo() == null) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "##### CADASTRAR BEM #####\n"));
                        m = update.updateId() + 1;

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o código do bem:"));
                        m = update.updateId() + 1;

                        bem.setCodigo(update.message().text());
                        controle_bem = true;
                    }
                    else if(bem.getNome() == null) {
                        bem.setCodigo(update.message().text());
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o nome do bem:"));
                        m = update.updateId() + 1;

                        bem.setNome(update.message().text());
                        controle_bem = true;
                    }
                    else if (bem.getDescricao() == null) {
                        bem.setNome(update.message().text());

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite a descrição do bem: "));
                        bem.setDescricao(update.message().text());
                    }
                    else if (nome_localizacao == null) {
                        bem.setDescricao(update.message().text());

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o nome da localização do bem: "));

                        nome_localizacao = update.message().text();
                    }
                    else if (nome_localizacao != "1") {
                        nome_localizacao = update.message().text();
                        localizacao = gerencia.buscaLocalizacaoPorNome(nome_localizacao);

                        if(localizacao != null){
                            bem.setLocalizacao(localizacao);
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o nome da categoria do bem: "));
                            nome_categoria = update.message().text();
                            nome_localizacao = "1";
                        }else {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "" +
                                    "# Localização inválida! Tente novamente."));
                            nome_localizacao = update.message().text();
                            localizacao = gerencia.buscaLocalizacaoPorNome(nome_localizacao);

                        }
                    }
                    else if (nome_categoria == null ) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite o nome da categoria do bem: "));

                        nome_categoria = update.message().text();
                        m = update.updateId() + 1;
                    }
                    else if (nome_categoria != null) {
                        nome_categoria = update.message().text();

                        categoria = gerencia.buscaCategoriaPorNome(nome_categoria);

                        if(categoria != null){
                            bem.setCategoria(categoria);
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Bem cadastrado com sucesso!\nCódigo: "+ bem.getCodigo()
                                    + "\nNome: " + bem.getNome() + "\nDescrição: "+ bem.getDescricao() + "\nLocalização: "
                                    + bem.getLocalizacao().getNome() + "\nCategoria: " + bem.getCategoria().getNome()));
                            gerencia.getBens().add(bem);

                            controle_bem = false;
                            nome_categoria = null;
                            nome_localizacao = null;
                            bem = new Bem(null, null, null, null, null);

                        }else {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "" +
                                    "# Categoria inválida! Tente novamente."));
                            nome_categoria = update.message().text();
                            categoria = gerencia.buscaCategoriaPorNome(nome_categoria);

                        }
                    }
                }


                //#################### 4 ESCOLHA ########################
                else if (respostaMenu.equals("/listar_localizacoes")) {
                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### LOCAIS CADASTRADOS ######"));

                    for (Localizacao local:gerencia.getLocalizacoes()) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- " +
                                local.getNome()));
                    }
                }

                //#################### 5 ESCOLHA ########################
                else if (respostaMenu.equals("/listar_categorias")) {
                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### CATEGORIAS CADASTRADOS ######"));

                    for (Categoria catega : gerencia.getCategorias()) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- " +
                                catega.getNome()));
                    }
                }

                //#################### 6 ESCOLHA ########################
                else if (respostaMenu.equals("/listar_bens_de_localizacao")) {
                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### BENS CADASTRADOS ######"));

                    for (Bem bem_listar:gerencia.getBens()) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- " +
                                bem_listar.getNome()));
                    }
                }

                //#################### 7 ESCOLHA ########################
                else if (respostaMenu.equals("/buscar_bem_por_codigo") || controle_busca) {
                    if (codigo == null){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### BUSCAR BEM POR CÓDIGO ######" +
                                "\n- Digite o código a ser buscado: "));
                        codigo = update.message().text();
                        controle_busca = true;

                    }
                    else if(codigo != "1"){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite 1 para confirmar a busca"));
                        codigo = update.message().text();
                        for (Bem busca_bem : gerencia.getBens()) {
                            if(codigo.equals(busca_bem.getCodigo())) {
                                controle_bens.add(busca_bem);
                            }
                        }
                        codigo = "1";
                    }
                    else if(!controle_bens.isEmpty()) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Bens cadastrados com o código # \n"));
                        for (Bem bens : controle_bens) {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Nome do bem: "
                                    + bens.getNome() + "\nDescrição: " + bens.getDescricao() + "\nLocalização: " +
                                    bens.getLocalizacao().getNome() + "\nCategoria: " + bens.getCategoria().getNome()));
                        }
                        controle_busca = false;
                        codigo = null;
                        controle_bens.clear();
                    }else {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Bens cadastrados com o código #\n"
                                + "- Nenhum bem está cadastrado com esse código. Tente novamente com "));
                        controle_busca = false;
                        codigo = null;
                        controle_bens.clear();
                    }

                }
                //#################### 8 ESCOLHA ########################
                else if (respostaMenu.equals("/buscar_bem_por_nome") || controle_nome) {
                    if (nome == null){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### BUSCAR BEM POR NOME ######\n" +
                                "- Digite o nome a ser buscado: "));
                        nome = update.message().text();
                        controle_nome = true;

                    }
                    else if(nome != "1"){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite 1 para confirmar a busca"));
                        nome = update.message().text();
                        for (Bem busca_bem : gerencia.getBens()) {
                            if(nome.equals(busca_bem.getNome())) {
                                controle_bens.add(busca_bem);
                            }
                        }
                        nome = "1";
                    }
                    else if(!controle_bens.isEmpty()) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Bens cadastrados com o nome pesquisado #\n"));
                        for (Bem bens : controle_bens) {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Código do bem: "
                                    + bens.getCodigo() + "\nDescrição: " + bens.getDescricao() + "\nLocalização: " +
                                    bens.getLocalizacao().getNome() + "\nCategoria: " + bens.getCategoria().getNome()));
                        }
                        controle_nome = false;
                        nome = null;
                        controle_bens.clear();
                    }else {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Bens cadastrados com o nome pesquisado #\n"
                                + "- Nenhum bem está cadastrado com esse nome." + "\n" + "\nPara retornar ao menu digite /menu"));
                        controle_nome = false;
                        nome = null;
                        controle_bens.clear();
                    }

                }
                //#################### 9 ESCOLHA ########################
                else if (respostaMenu.equals("/buscar_bem_por_descricao") || controle_descricao) {
                    if (descricao == null){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### BUSCAR BEM POR DESCRIÇÃO ######\n" +
                                "- Digite a descrição a ser buscado: "));
                        descricao = update.message().text();
                        controle_descricao = true;

                    }
                    else if(descricao != "1"){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite 1 para confirmar a busca"));
                        descricao = update.message().text();
                        for (Bem busca_bem : gerencia.getBens()) {
                            if(descricao.equals(busca_bem.getDescricao())) {
                                controle_bens.add(busca_bem);
                            }
                        }
                        descricao = "1";
                    }
                    else if(!controle_bens.isEmpty()) {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Bens cadastrados com a descricao pesquisada #\n"));
                        for (Bem bens : controle_bens) {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Código do bem: "
                                    + bens.getCodigo() + "\nNome: " + bens.getNome() + "\nLocalização: " +
                                    bens.getLocalizacao().getNome() + "\nCategoria: " + bens.getCategoria().getNome()));
                        }
                        controle_descricao = false;
                        descricao = null;
                        controle_bens.clear();
                    }else {
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# Bens cadastrados com a descrição pesquisada #\n"
                                + "- Nenhum bem está cadastrado com essa descrição."));
                        controle_descricao = false;
                        descricao = null;
                        controle_bens.clear();
                    }
                }
                //#################### 10 ESCOLHA ########################
                else if (respostaMenu.equals("/movimentar_bem") || controle_movimenta) {
                    if (codigo == null){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### MOVIMENTAÇÃO DE BEM ######\n" +
                                "- Digite o código do bem desejado: "));
                        codigo = update.message().text();
                        controle_movimenta = true;

                    }
                    else if(nome_localizacao == null){
                        codigo = update.message().text();
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- Digite a localização para onde deseja mover: "));
                        nome_localizacao = update.message().text();
                    }

                    else if(codigo != null && nome_localizacao != null) {
                        nome_localizacao = update.message().text();

                        for (Bem checa_bem : gerencia.getBens()) {
                            if(codigo.equals(checa_bem.getCodigo())) {
                                checa_codigo = true;
                            }else {
                                checa_codigo = false;
                            }
                        }

                        for (Localizacao busca_localizacao : gerencia.getLocalizacoes()) {
                            if(nome_localizacao.equals(busca_localizacao.getNome())) {
                                checa_localizacao = true;
                                localizacao = busca_localizacao;
                            }else {
                                checa_localizacao = false;
                            }
                        }
                        System.out.println("codigo = " + checa_codigo + "\nlocal = " + checa_localizacao);
                        if(checa_codigo == true && checa_localizacao == true){
                            gerencia.movimentaBem(codigo, localizacao);
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# O bem foi movimentado para "
                                    + localizacao.getNome() + " com sucesso!"));
                            codigo = null;
                            nome_localizacao = null;
                            localizacao = null;
                            controle_movimenta = false;
                            checa_codigo = false;
                            checa_localizacao = false;
                        }else {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "" +
                                    "- O código ou localização não foi encontrado nos registros. Tente novamente com /movimentar_bem."));
                            codigo = null;
                            nome_localizacao = null;
                            localizacao = null;
                            controle_movimenta = false;
                        }
                    }
                }

                //#################### 11 ESCOLHA ########################
                else if (respostaMenu.equals("/gerar_relatorio")) {
                    if (nome_localizacao == null){
                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "###### RELATÓRIO PATRIMONIAL ######" +
                                "\n\n# POR LOCAL #"));

                        for (Localizacao busca_local : gerencia.getLocalizacoes()) {
                            controle_local_busca.add(busca_local);
                        }

                        for (Localizacao local : controle_local_busca) {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- "+local.getNome()));
                            for (Bem bens : gerencia.getBens()) {
                                if(local.getNome().equals(bens.getLocalizacao().getNome())) {
                                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Nome do bem: "
                                            + bens.getNome() + "\nDescrição: " + bens.getDescricao() + "\nLocalização: " +
                                            bens.getLocalizacao().getNome() + "\nCategoria: " + bens.getCategoria().getNome()));
                                }
                            }
                        }

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# POR CATEGORIA #"));

                        for (Categoria busca_categoria : gerencia.getCategorias()) {
                            controle_categoria_busca.add(busca_categoria);
                        }

                        for (Categoria categoria_busca : controle_categoria_busca) {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- "+categoria_busca.getNome()));
                            for (Bem bens : gerencia.getBens()) {
                                if(categoria_busca.getNome().equals(bens.getCategoria().getNome())) {
                                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Nome do bem: "
                                            + bens.getNome() + "\nDescrição: " + bens.getDescricao() + "\nLocalização: " +
                                            bens.getLocalizacao().getNome() + "\nCategoria: " + bens.getCategoria().getNome()));
                                }
                            }
                        }

                        sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "# POR NOME #"));

                        for (Bem busca_bem : gerencia.getBens()) {
                            if(!controle_nome_busca.contains(busca_bem.getNome()))
                                controle_nome_busca.add(busca_bem.getNome());
                        }

                        for (String nome_busca : controle_nome_busca) {
                            sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "- " + nome_busca));
                            for (Bem bens : gerencia.getBens()) {
                                if(nome_busca.equals(bens.getNome())) {
                                    sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Nome do bem: "
                                            + bens.getNome() + "\nDescrição: " + bens.getDescricao() + "\nLocalização: " +
                                            bens.getLocalizacao().getNome() + "\nCategoria: " + bens.getCategoria().getNome()));
                                }
                            }
                        }

                        controle_local_busca.clear();
                        controle_categoria_busca.clear();
                        controle_nome_busca.clear();
                        localizacao = new Localizacao(null, null);
                        categoria = new Categoria(null, null, null);

                    }

                }

                //envio de "Escrevendo" antes de enviar a resposta
                baseResponse = bot.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
                //verificação de ação de chat foi enviada com sucesso
                System.out.println("Resposta de Chat Action Enviada?" + baseResponse.isOk());

            }
        }
    }
}
