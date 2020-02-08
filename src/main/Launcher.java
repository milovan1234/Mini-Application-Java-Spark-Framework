package main;

import com.google.gson.Gson;
import models.Automobil;
import models.Data;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static spark.Spark.*;

public class Launcher {
    public static void main(String[] args) {
        String projectDir = System.getProperty("user.dir");
        String staticDir = "/src/public";
        staticFiles.externalLocation(projectDir + staticDir);
        //staticFiles.location("/public");

        port(5000);
        String path="src/public/json/automobili.json";

        HashMap<String,Object> polja=new HashMap<>();

        //Ruta za početnu stranu
        //GET: /
        get("/", (request, response) -> {
            if(request.session().attribute("prijavljen") != null){
                polja.put("prijavljen",true);
            }
            else {
                polja.put("prijavljen",null);
            }
            HashSet<String> marke = new HashSet<>();
            Data.readFromJson(path).forEach(x -> marke.add(x.getMarka()));
            polja.put("marke", marke);
            polja.put("automobili",Data.readFromJson(path));
            return new ModelAndView(polja,"index.hbs");
        },new HandlebarsTemplateEngine());

        //Rute koje pocinju sa /api
        path("/api",() -> {

            //Ruta koja vraca sve modele za odabranu marku
            //POST: /api/vratiModeleZaMarku
            post("/vratiModeleZaMarku",(request, response) -> {
                String marka = request.queryParams("marka");
                HashSet<String> modeli = new HashSet<>();
                for(Automobil a : Data.readFromJson(path)){
                    if(a.getMarka().equals(marka)){
                        modeli.add(a.getModel());
                    }
                }
                return new Gson().toJson(modeli);
            });

            //Ruta koja vrsi pretragu na osnovu odabranog
            //POST: /api/vratiPretragu
            post("/vratiPretragu",(request, response) -> {
                String marka = request.queryParams("marka");
                String model = request.queryParams("model");
                String karoserija = request.queryParams("karoserija");
                String gorivo = request.queryParams("gorivo");
                String menjac = request.queryParams("menjac");
                ArrayList<Automobil> automobili = Data.readFromJson(path);
                if(!marka.equals("null")){
                    for(int i=0;i<automobili.size();i++){
                        if(!automobili.get(i).getMarka().equals(marka)){
                            automobili.remove(i);
                            i--;
                        }
                    }
                }
                if(!model.equals("null")){
                    for(int i=0;i<automobili.size();i++){
                        if(!automobili.get(i).getModel().equals(model)){
                            automobili.remove(i);
                            i--;
                        }
                    }
                }
                if(!karoserija.equals("null")){
                    for(int i=0;i<automobili.size();i++){
                        if(!automobili.get(i).getKaroserija().equals(karoserija)){
                            automobili.remove(i);
                            i--;
                        }
                    }
                }
                if(!gorivo.equals("null")){
                    for(int i=0;i<automobili.size();i++){
                        if(!automobili.get(i).getGorivo().equals(gorivo)){
                            automobili.remove(i);
                            i--;
                        }
                    }
                }
                if(!menjac.equals("null")){
                    for(int i=0;i<automobili.size();i++){
                        if(!automobili.get(i).getMenjac().equals(menjac)){
                            automobili.remove(i);
                            i--;
                        }
                    }
                }
                return new Gson().toJson(automobili);
            });

        });

        //Ruta detaljnije o Automobilu
        //GET /detaljnije/id
        get("/detaljnije/:id",(request, response) -> {
            if(request.session().attribute("prijavljen") != null){
                polja.put("prijavljen",true);
            }
            else {
                polja.put("prijavljen",null);
            }
            int id = Integer.parseInt(request.params("id"));
            polja.put("detaljnije",Data.readFromJson(path).stream().filter(x -> x.getId() == id).findFirst().orElse(null));
            return new ModelAndView(polja,"detailsPage.hbs");
        }, new HandlebarsTemplateEngine());

        //Ruta strane za prijavu koja redirektuje na pocetnu stranu ako je Administrator prijavljen
        //GET: /prijavi-se
        get("/prijavi-se",(request, response) -> {
            if(request.session().attribute("prijavljen") != null){
                response.redirect("/");
                return null;
            }
            polja.put("poruka",null);
            return new ModelAndView(polja,"loginPage.hbs");
        }, new HandlebarsTemplateEngine());

        //Ruta za prijavu
        //POST: /prijava
        post("/prijava",(request, response) -> {
            String korisnicko = request.queryParams("korisnicko");
            String lozinka = request.queryParams("lozinka");
            if(korisnicko.equals("admin") &&  lozinka.equals("admin")){
                request.session().attribute("prijavljen",true);
                response.redirect("/admin-strana");
                return null;
            }
            polja.put("poruka","Uneto podaci nisu ispravni! Pokušajte ponovo.");
            return  new ModelAndView(polja,"loginPage.hbs");
        }, new HandlebarsTemplateEngine());

        //Ruta za Administratorsku stranu
        //GET: /admin-strana
        get("/admin-strana",(request, response) -> {
            if(request.session().attribute("prijavljen") == null){
                response.redirect("/");
                return null;
            }
            polja.put("automobili",Data.readFromJson(path));
            return new ModelAndView(polja,"adminPage.hbs");
        }, new HandlebarsTemplateEngine());



        //Ruta za odjavljivanje Administratora
        //GET: /odjava
        get("/odjava",(request, response) -> {
            request.session().invalidate();
            response.redirect("/");
            return null;
        });


        //Ruta za pretragu Automobila po marki ili modelu na Administratorskoj strani
        //POST: /pretragaAutomobilaAdmin
        post("/pretragaAutomobilaAdmin",(request, response) -> {
            String pretraga = request.queryParams("pretraga");
            return new Gson().toJson(Data.readFromJson(path).stream().filter(x -> x.getMarka().toLowerCase().contains(pretraga.toLowerCase())
                    || x.getModel().toLowerCase().contains(pretraga.toLowerCase())).toArray());
        });


        //Ruta na kojoj se vrsi brisanje odabranog Automobila
        //POST: /brisanjeAutomobilaAdmin
        post("/brisanjeAutomobilaAdmin",(request, response) -> {
            int id = Integer.parseInt(request.queryParams("id"));
            ArrayList<Automobil> automobili = Data.readFromJson(path);
            for(int i=0;i<automobili.size();i++){
                if(automobili.get(i).getId() == id){
                    automobili.remove(i);
                    i--;
                }
            }
            Data.writeToJSON(automobili,path);
            return new Gson().toJson(automobili);
        });


        //Ruta koja prikazuje formu za dodavanje automobila
        //GET: /novi-automobil
        get("/novi-automobil",(request, response) -> {
            if(request.session().attribute("prijavljen") == null){
                response.redirect("/");
                return null;
            }
            polja.put("novi",true);
            polja.put("izmena",null);
            polja.put("automobil",null);
            return new ModelAndView(polja,"automobilsPage.hbs");
        }, new HandlebarsTemplateEngine());


        //Ruta koja prikazuje formu za izmenu Automobila
        //GET: /izmeni-automobil/id
        get("/izmeni-automobil/:id",(request, response) -> {
            if(request.session().attribute("prijavljen") == null){
                response.redirect("/");
                return null;
            }
            int id = Integer.parseInt(request.params("id"));
            polja.put("novi",null);
            polja.put("izmena",true);
            polja.put("automobil",Data.readFromJson(path).stream().filter(x -> x.getId() == id).findFirst().orElse(null));
            return new ModelAndView(polja,"automobilsPage.hbs");
        }, new HandlebarsTemplateEngine());


        //Ruta koja dodaje novi Automobil u json fajl
        //POST: /dodajNoviAutomobil
        post("/dodajNoviAutomobil",(request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));
            String imeFajla = request.raw().getPart("slika").getSubmittedFileName();
            Path tempFile = Paths.get("./src/public/img/" + request.raw().getPart("slika").getSubmittedFileName());
            try (InputStream input = request.raw().getPart("slika").getInputStream()) {
                if(!Files.exists(tempFile))
                    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            String marka = request.queryMap("marka").value();
            String model = request.queryMap("model").value();
            int cena = Integer.parseInt(request.queryMap("cena").value());
            int godiste = Integer.parseInt(request.queryMap("godiste").value());
            int kubikaza = Integer.parseInt(request.queryMap("kubikaza").value());
            String karoserija = request.queryMap("karoserija").value();
            String menjac = request.queryMap("menjac").value();
            String gorivo = request.queryMap("gorivo").value();
            ArrayList<Automobil> automobili = Data.readFromJson(path);
            automobili.add(new Automobil(automobili.get(automobili.size()-1).getId()+1,marka,model,cena,godiste,kubikaza,karoserija,gorivo,menjac,"img/"+imeFajla));
            Data.writeToJSON(automobili,path);
            return new Gson().toJson("Uspesno dodat Automobil!");
        });

        //Ruta koja menja sliku Automobila
        //POST: /izmeniSlikuAutomobila
        post("/izmeniSlikuAutomobila",(request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));
            String imeFajla = request.raw().getPart("slika").getSubmittedFileName();
            Path tempFile = Paths.get("./src/public/img/" + request.raw().getPart("slika").getSubmittedFileName());
            try (InputStream input = request.raw().getPart("slika").getInputStream()) {
                if(!Files.exists(tempFile))
                    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
            int id = Integer.parseInt(request.queryMap("id").value());
            ArrayList<Automobil> automobili = Data.readFromJson(path);
            for(Automobil a : automobili){
                if(a.getId() == id){
                    a.setSlika("img/" + imeFajla);
                    break;
                }
            }
            Data.writeToJSON(automobili,path);
            return new Gson().toJson("../img/"+ imeFajla);
        });

        //Ruta koja vrsi izemnu podataka Automobila
        //POST: /izmeniPodatkeAutomobil
        post("/izmeniPodatkeAutomobil",(request, response) -> {
            int id = Integer.parseInt(request.queryParams("id"));
            String marka = request.queryParams("marka");
            String model = request.queryParams("model");
            int cena = Integer.parseInt(request.queryParams("cena"));
            int godiste = Integer.parseInt(request.queryParams("godiste"));
            int kubikaza = Integer.parseInt(request.queryParams("kubikaza"));
            String karoserija = request.queryParams("karoserija");
            String menjac = request.queryParams("menjac");
            String gorivo = request.queryParams("gorivo");
            ArrayList<Automobil> automobili = Data.readFromJson(path);
            Automobil automobil = null;
            for(Automobil a : automobili){
                if(a.getId() == id){
                    a.setMarka(marka);
                    a.setModel(model);
                    a.setCena(cena);
                    a.setGodiste(godiste);
                    a.setKubikaza(kubikaza);
                    a.setKaroserija(karoserija);
                    a.setMenjac(menjac);
                    a.setGorivo(gorivo);
                    automobil = a;
                    break;
                }
            }
            Data.writeToJSON(automobili,path);
            return new Gson().toJson(automobil);
        });


//        ArrayList<Automobil> automobili = new ArrayList<>();
//		automobili.add(new Automobil(1,"Volkswagen","Golf 7", 14290,2016,1598,"Hečbek",
//				"Dizel","Automatski","img/vw-golf7-1.jpg"));
//		automobili.add(new Automobil(2,"Fiat","500", 4100,2008,1242,"Hečbek",
//				"Benzin","Manuelni","img/fiat-500-1.jpg"));
//		automobili.add(new Automobil(3,"Audi","Q5", 48900,2019,1968,"Džip/SUV",
//				"Dizel","Automatski","img/audi-q5-1.jpg"));
//		Data.writeToJSON(automobili,path);
    }
}
