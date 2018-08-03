package principal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import arquivo.Ancora;

public class LeitorArquivo {

	public static void main(String[] args) {

		Scanner sc = null;
		Scanner scHtml = null;
		String linha;
		List<Produto> produtos = new ArrayList<>();

		try {
			sc = new Scanner(Ancora.class.getResourceAsStream("estoque.csv"));
			sc.nextLine();

			while (sc.hasNextLine()) {
				linha = sc.nextLine();
				String[] aux = linha.split(";");
				String nome = aux[0];
				String marca = aux[1];
				Double preco = new Double(aux[2]);
				Integer estoque = Integer.parseInt(aux[3]);

				Produto produto = new Produto();
				produto.setNome(nome);
				produto.setMarca(marca);
				produto.setPreco(preco);
				produto.setEstoque(estoque);

				produtos.add(produto);
			}

			Collections.sort(produtos, new Comparator<Produto>() {
				@Override
				public int compare(Produto produto1, Produto produto2) {
					return produto2.getPreco().compareTo(produto1.getPreco());
				}
			});

			NumberFormat nf = NumberFormat.getCurrencyInstance();

			Produto produto1 = produtos.get(0);
			Produto produto2 = produtos.get(1);

			System.out.println(produto1.getNome() + ", " + nf.format(produto1.getPreco()));
			System.out.println(produto2.getNome() + ", " + nf.format(produto2.getPreco()));

			Double precoTotal = 0D;
			Integer quantidadeTotal = 0;

			StringBuilder sbTabela = new StringBuilder();

			for (Produto produto : produtos) {
				precoTotal += produto.getPreco();
				quantidadeTotal++;

				sbTabela.append("<tr>");
				sbTabela.append("<td>").append(produto.getNome()).append("</td>");
				sbTabela.append("<td>").append(produto.getMarca()).append("</td>");
				sbTabela.append("<td>").append(nf.format(produto.getPreco())).append("</td>");
				sbTabela.append("<td>").append(produto.getEstoque()).append("</td>");
				sbTabela.append("</tr>");
			}

			scHtml = new Scanner(Ancora.class.getResourceAsStream("extra.html"), "ISO-8859-1");
			StringBuilder sb = new StringBuilder();

			while (scHtml.hasNextLine()) {
				sb.append(scHtml.nextLine());
			}

			DateFormat dfmt = new SimpleDateFormat("dd/MM/yyyy 'as' HH:mm:ss");

			String arquivoHtml = sb.toString();
			arquivoHtml = arquivoHtml.replace("#lista_produtos", sbTabela.toString());
			arquivoHtml = arquivoHtml.replace("#data_geracao", dfmt.format(new Date()));
			String caminho = Ancora.class.getResource("extra.html").toString().replaceAll("extra.html", "").replaceAll("file:/", "").replaceAll("bin", "src");
			File file = new File(caminho + "estoque.html");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(arquivoHtml);
			writer.flush();
			writer.close();

			Runtime.getRuntime().exec("explorer.exe " + caminho.replace("/", "\\"));
			Double precoMedio = precoTotal / quantidadeTotal;

			System.out.println("Preço médio de produtos: " + nf.format(precoMedio));
			System.out.println("Quantidade total de itens em estoque: " + quantidadeTotal);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Ocorreu um erro ao ler o arquivo.");
		} finally {
			try {
				sc.close();
				scHtml.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
