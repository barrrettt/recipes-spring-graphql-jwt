package com.example.recipes.data;

import java.util.Arrays;
import java.util.HashSet;

import com.example.recipes.data.entity.Recipe;
import com.example.recipes.data.entity.Role;
import com.example.recipes.data.entity.Tag;
import com.example.recipes.data.entity.User;
import com.example.recipes.data.repos.RecipeRepo;
import com.example.recipes.data.repos.RolesRepo;
import com.example.recipes.data.repos.TagRepo;
import com.example.recipes.data.repos.UserRepo;
import com.example.recipes.security.SecurityDataFetcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ToolsJPA {

	//repos
	@Autowired
	UserRepo userR;
	@Autowired
	RolesRepo rolesR;
	@Autowired 
	RecipeRepo recipesR; 
	@Autowired
	TagRepo tagR;

	//security(init require berfore db poblate)
	@Autowired
	private SecurityDataFetcher security;

	// init data
    public void initDataBase(){
        
		//3 roles:
		rolesR.save(new Role("ADMIN"));
		rolesR.save(new Role("SUPER"));
		rolesR.save(new Role("USER"));

		//ADMIN User
		userR.save(new User("admin","admin@email.com","admin"));

		//ADD ROLE
		User admin = userR.findFirstByName("admin");
		Role rAdmin = rolesR.findFirstByName("ADMIN");
		admin.setRoles(new HashSet<>(Arrays.asList(rAdmin)));
		userR.save(admin);

		//init my security
		security.init();
    }

	//mobs to play
	public void createMobs(){

		Role rSuper =rolesR.findFirstByName("SUPER");
		Role rUser =rolesR.findFirstByName("USER");

		//users
		String pass = "pass";//same for all mobs
		userR.save(new User("super","2email@email.com",pass));
		userR.save(new User("Isa loca","3email@email.com",pass));
		userR.save(new User("Alex Kid","4email@email.com",pass));
		userR.save(new User("John Snow","5email@email.com",pass));
		userR.save(new User("user","6email@email.com",pass));
		userR.save(new User("Eliot Smith","7email@email.com",pass));
		userR.save(new User("Dua Lipa","8email@email.com",pass));

		//roles to users
		int i = 0;
		for (User user : userR.findAll()) {
			if (i==0)continue;
			if (i<4){
				user.getRoles().add(rSuper);
			}else{
				user.getRoles().add(rUser);
			}
			userR.save(user);
			i++;
		}

		//random recipes
		//La tortilla del super
		User user = userR.findFirstByNameContaining("Isa");
		Recipe recipe = new Recipe("Tortilla de patatas",user);
		recipe.setIngredients("Patatas 2 grandes.\nHuevos 4.\nAceite para cubrir las patatas.\nSal una pizca.");
		recipe.setInstruccions("Cortar la patatas en láminas.\nPoner el aceite a calentar.\nMeter las patatas en aceite y dejar hasta que estén hechas.\nBatir el huevo en un bol y cuando estén las patatas mezclar todo.\nFinalmente poner la mezcla en una sarten con una pizca de aceite\nVoltear la tortilla cuando sea necesario para hacerla por el otro lado.\nComer.");
		user.getRecipes().add(recipe);

		//Ensalada
		recipe = new Recipe("Ensalada clásica",user);
		recipe.setIngredients("Lechuga.\nTomate.\nCebolla\nSal, aceite y vinagre.");
		recipe.setInstruccions("Lavar lechuga.\nCortar tomate y cebolla.\nPoner todo en un bol y alinear.");
		user.getRecipes().add(recipe);
		userR.save(user);

		// Inferno de dante
		user = userR.findFirstByNameContaining("Eliot");
		recipe = new Recipe("Patatas inferno de Dante",user);
		recipe.setIngredients("Patatas.\nSalsa picante mejicana.");
		recipe.setInstruccions("Cortar la patata en tiras.\nFreir.\nTodo a una fuente y mezclar.");
		user.getRecipes().add(recipe);
		userR.save(user);

		// Guisantes con jamon de Alex
		user = userR.findFirstByNameContaining("Alex");
		recipe = new Recipe("Guisantes con jamón en olla GM",user);
		recipe.setIngredients("Guisantes 200g.\nTacos pequeños de jamón\nCebolla 1/2.");
		recipe.setInstruccions("Sofrito con la cebolla hasta dorar.\nMeter el resto con un vaso de agua.\nMenú turbo 5 min.");
		user.getRecipes().add(recipe);

		// pasta con atun de alex
		recipe = new Recipe("Pasta con atún en olla GM",user);
		recipe.setIngredients("Pata 200g.\nAtún en lata grande\nAceitunas verden en bolas pequeña\nCebolla 1/2.\nTomate frito bote pequeño.");
		recipe.setInstruccions("Sofrito con la cebolla hasta dorar.\nMeter atún, aceitunas y tomate para mezclaer bien durante unos segundos\nAgua hasta cubrir y sal.\nMenú pasta.");
		user.getRecipes().add(recipe);

		// Arroz con leche de Alex
		recipe = new Recipe("Arroz con leche en olla GM",user);
		recipe.setIngredients("Arroz de postre 300g.\nLeche medio litro.\nAzucar 100g\nCanela en rama y en polvo.");
		recipe.setInstruccions("Meter todo en la pota y ala");
		user.getRecipes().add(recipe);
		userR.save(user);

		// Recetas Peter 
		user = userR.findFirstByNameContaining("user");
		recipe = new Recipe("Cebollas rellenas de cordero al horno",user);
		recipe.setIngredients("10 cebollas medianas\n500 g de carne de cordero picada\n1 zanahoria grande\n1 pimiento verde pequeño\n1 pimiento rojo\n2 tomates maduro\n4 dientes de ajo\nPerejil fresco\n100 ml. de aceite de oliva virgen extra\n100 ml. de vino blanco o sidra\nQueso rallado para gratinar\nHiervas aromáticas: Romero, tomillo y salvia (molidos y secos) 1/2 cucharadita de cada una\nPimienta negra recién molida y sal (al gusto de casa)");
		recipe.setInstruccions("Las cebollas son un ingrediente fundamental en nuestra gastronomía. Siempre se utilizan como acompañamiento para dar sabor, como en los deliciosos chipirones encebollados o la siempre apetecible empanada de morcilla y cebolla caramelizada, e incluso las podemos ver como ingredientes principal en platos como la tradicional sopa de cebolla francesa.\nSiguiendo esta tendencia francesa, hoy os traemos un plato que pone a las cebollas como ingrediente principal, las cebollas rellenas de cordero. Es un plato que viene de la cocina francesa y es una auténtica maravilla. Lo podemos servir tanto como plato principal como un acompañamiento espectacular de carnes o pescados. La receta es bastante sencilla y el resultado nos recordará a otros platos como los irresistibles champiñones rellenos de carne o los extraordinarios calabacines rellenos de mejillones, donde los vegetales son rellenados por distintos ingredientes dando unos sabores deliciosos.\n\nSi buscáis una receta distinta con la que sorprender a invitados o familiares, no le deis mas vueltas. Las cebollas rellenas de cordero son perfectas para esas ocasiones. Fáciles de hacer, no mucho tiempo de preparación… No hay excusa para no ponerse manos a la obra con ellas, porque están de rechupete.\n\nPreparación del relleno de cordero y las cebollas\nPrimero pelamos las cebollas y les cortamos la parte de arriba. Vaciamos el interior de la cebolla con una cuchara con cuidado y lo reservamos. Si no disponemos de ninguna herramienta específica se puede hacer perfectamente con una cucharilla. Es conveniente dejar sólo dos capas. Reservamos también esos trozos de cebolla que retiramos del interior.\nPelamos la zanahoria y los dientes de ajo, cortamos en trocito muy finos o pequeños. Lavamos los pimientos y el tomate. Cortamos el pimiento rojo y verde muy finito. El tomate lo vamos a rallar, reservamos todo lo necesario para nuestro sofrito.\nEchamos un poco de aceite de oliva virgen extra en una sartén y comenzamos a sofreír la parte que hemos retirado de la cebolla. Cuando la cebolla coja color, añadimos la zanahoria, el ajo, el pimiento verde y el pimiento rojo. Pasados unos minutos, añadimos el tomate rallado y lo vamos removiendo durante 10 minutos. Pasado este tiempo, añadimos la carne de cordero previamente picada con el perejil y las aromáticas. Salpimentamos al gusto.\nAñadimos el vino blanco, removemos y juntamos sabores e ingredientes. Lo dejamos hacerse durante 20 minutos a fuego medio bajo, sin prisa, queremos que el cordero quede bien cocinado. Vamos probando de sal, y añadimos un poco más si hiciese falta.\nCuando esté listo lo retiramos del fuego y lo dejamos enfriar unos minutos. Rellenamos las cebollas con el sofrito que acabamos de hacer y las colocamos en un recipiente apto para el horno o en la bandeja de horno. Precalentamos el horno a 190º C, 10 minutos antes de meter las cebollas en el horno.\nHorneado y presentación final de cebollas rellenas de cordero\nCubrimos el recipiente o bandeja con papel de aluminio, cerrándolo para que se hagan antes con su propio vapor.\nLas metemos en el horno y horneamos a 190º C durante 30 minutos. Pasado ese tiempo quitamos con mucho cuidado el papel de aluminio, porque sale vapor y te puedes quemar.\nEspolvoreamos un poco de tu queso rallado preferido por encima y gratinamos dos minutos (función gratinador) para que cojan un color bonito. Pasado este tiempo, las servimos en platos y echamos por encima tomillo fresco o perejil. Listas para comer.\nComo acompañamiento el arroz blanco me encanta, pero también deberían funcionar bien unas buenas patatas cocidas. Todo lo que ayude a disfrutar de esta salsa sin quitarle el protagonismo a las cebollas será bienvenido. ¡Qué aproveche!\n\nConsejos para unas cebollas rellenas de cordero perfectas.\nLa receta admite múltiples variantes, siempre que se base en la cocción larga de las cebollas. En vez de sidra se puede utilizar un chorro de vino blanco de casi cualquier variedad, incluso algo dulces.\n¿El tiempo de horneado? Depende de lo tiernas que sean las cebollas, las dulces o blancas suelen hacerse antes pues son más tiernas, las que hemos empleado, eran cebollas normales y en media hora estaban perfectas para comer.\nSi no nos gusta la carne de cordero, la podemos sustituir por cerdo, ternera o pavo sin problema.\nEstas cebollas se pueden congelar perfectamente, y aguantan hasta 6 meses\nSi no tenemos tomillo o perejil, podemos echar por encima un poco de ajo en polvo.");
		user.getRecipes().add(recipe);

		recipe = new Recipe("Receta fantasticas",user);
		recipe.setIngredients("Lista de ingredientes fantasticos.");
		recipe.setInstruccions("Super instrucciones para que todo quede genial.");
		user.getRecipes().add(recipe);
		userR.save(user);

		// more mobs
		user = userR.findFirstByNameContaining("Dua");
		recipe = new Recipe("Pasta con salsa de ricotta y albahaca",user);
		recipe.setIngredients("Lista de ingredientes");
		recipe.setInstruccions("Instrucciones.");
		user.getRecipes().add(recipe);

		recipe = new Recipe("Pasta con champiñones y queso Parmesano",user);
		recipe.setIngredients("Lista de ingredientes geniales.");
		recipe.setInstruccions("Como lo anterior pero mas rico.");
		user.getRecipes().add(recipe);
		
		recipe = new Recipe("Schiafoni alla sorrentina",user);
		recipe.setIngredients("Esta lista es la leche.");
		recipe.setInstruccions("Esta ya es la última que no se me ocurre nada mejor..");
		user.getRecipes().add(recipe);
		userR.save(user);

		//Tags
		tagR.saveAll(Arrays.asList(
			new Tag("GM"),
			new Tag("TRADICIONAL"),
			new Tag("THERMOMIX"),
			new Tag("POSTRE"),
			new Tag("RAPIDO"),
			new Tag("FIESTAS"),
			new Tag("FAVORITO")
		));
		
		//recipes + tags
		recipe = recipesR.findFirstByNameContaining("tortilla");
		recipe.getTags().add(tagR.findFirstByName("FAVORITO"));
		recipe.getTags().add(tagR.findFirstByName("TRADICIONAL"));
		recipesR.save(recipe);

		recipe = recipesR.findFirstByNameContaining("Ensalada");
		recipe.getTags().add(tagR.findFirstByName("FAVORITO"));
		recipe.getTags().add(tagR.findFirstByName("RAPIDO"));
		recipesR.save(recipe);

		recipe = recipesR.findFirstByNameContaining("Guisantes");
		recipe.getTags().add(tagR.findFirstByName("GM"));
		recipe.getTags().add(tagR.findFirstByName("RAPIDO"));
		recipesR.save(recipe);

		recipe = recipesR.findFirstByNameContaining("Pasta");
		recipe.getTags().add(tagR.findFirstByName("FAVORITO"));
		recipe.getTags().add(tagR.findFirstByName("GM"));
		recipe.getTags().add(tagR.findFirstByName("RAPIDO"));
		recipesR.save(recipe);

		recipe = recipesR.findFirstByNameContaining("Arroz");
		recipe.getTags().add(tagR.findFirstByName("FAVORITO"));
		recipe.getTags().add(tagR.findFirstByName("GM"));
		recipe.getTags().add( tagR.findFirstByName("POSTRE"));
		recipesR.save(recipe);

		recipe = recipesR.findFirstByNameContaining("Pasta con salsa");
		recipe.getTags().add(tagR.findFirstByName("FIESTAS"));
		recipe.getTags().add(tagR.findFirstByName("POSTRE"));
		recipe.getTags().add( tagR.findFirstByName("THERMOMIX"));
		recipesR.save(recipe);

		recipe = recipesR.findFirstByNameContaining("Pasta con champiñones");
		recipe.getTags().add(tagR.findFirstByName("FIESTAS"));
		recipesR.save(recipe);

		recipe = recipesR.findFirstByNameContaining("Schiafoni alla sorrentina");
		recipe.getTags().add(tagR.findFirstByName("POSTRE"));
		recipe.getTags().add( tagR.findFirstByName("THERMOMIX"));
		recipesR.save(recipe);
	}

	//reset all
	public void resetDataBase(){

		//delete all
		userR.deleteAll();
		rolesR.deleteAll();
		tagR.deleteAll();
		recipesR.deleteAll();

		//database allwais need this: 1 admin user and 3 roles
		initDataBase();
	}

}
