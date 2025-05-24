import * as functions from "firebase-functions";

export const getMercadoPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBarcelona = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/FcBarcelona/";
    const storageLiverpool = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Liverpool/";
    const storageAtletico = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/AtleMadrid/";
    const storageMadrid = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/RMadrid/";
    const storageArsenal = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Arsenal/";
    const storageChelsea = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Chelsea/";
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/mercado/";

    const mercadoPlayers = [
      // Porteros (6)
      {
        name: "Marc-André ter Stegen",
        position: "Portero",
        overall: 89,
        precio: 22000000,
        url: `${storageBarcelona}TerStegen.png`,
      },
      {
        name: "Alisson Becker",
        position: "Portero",
        overall: 89,
        precio: 22000000,
        url: `${storageLiverpool}Alisson.png`,
      },
      {
        name: "Mike Maignan",
        position: "Portero",
        overall: 87,
        precio: 20000000,
        url: `${storageBaseUrl}Maignan.png`,
      },
      {
        name: "Jan Oblak",
        position: "Portero",
        overall: 88,
        precio: 20000000,
        url: `${storageAtletico}OBLAK.png`,
      },
      {
        name: "Gregor Kobel",
        position: "Portero",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Kobel.webp`,
      },
      {
        name: "Gianluigi Donnarumma",
        position: "Portero",
        overall: 87,
        precio: 20000000,
        url: `${storageBaseUrl}Donnarumma.png`,
      },

      // Defensores (14)
      {
        name: "Ronald Araújo",
        position: "Defensa",
        overall: 86,
        precio: 20000000,
        url: `${storageBarcelona}araujo.png`,
      },
      {
        name: "William Saliba",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Saliba.png`,
      },
      {
        name: "Theo Hernández",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Hernández.png`,
      },
      {
        name: "Achraf Hakimi",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Hakimi.png`,
      },
      {
        name: "Alphonso Davies",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Davies.png`,
      },
      {
        name: "Dayot Upamecano",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Upamecano.png`,
      },
      {
        name: "Jules Koundé",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBarcelona}Kounde.png`,
      },
      {
        name: "Giovanni Di Lorenzo",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Lorenzo.webp`,
      },
      {
        name: "Jeremie Frimpong",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Frimpong.png`,
      },
      {
        name: "Antonio Rüdiger",
        position: "Defensa",
        overall: 86,
        precio: 15000000,
        url: `${storageMadrid}Rudiger.png`,
      },
      {
        name: "Raphaël Varane",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Varane.png`,
      },
      {
        name: "Matthijs de Ligt",
        position: "Defensa",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Ligt.png`,
      },
      {
        name: "Milan Škriniar",
        position: "Defensa",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Škriniar.png`,
      },
      {
        name: "Benjamin Pavard",
        position: "Defensa",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Pavard.png`,
      },

      // Centrocampistas (16)
      {
        name: "Frenkie de Jong",
        position: "Mediocentro",
        overall: 86,
        precio: 20000000,
        url: `${storageBarcelona}DeJong.png`,
      },
      {
        name: "Martin Ødegaard",
        position: "Mediocentro",
        overall: 87,
        precio: 20000000,
        url: `${storageArsenal}Ødegaard.png`,
      },
      {
        name: "Sandro Tonali",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Tonali.png`,
      },
      {
        name: "Warren Zaïre-Emery",
        position: "Mediocentro",
        overall: 78,
        precio: 5000000,
        url: `${storageBaseUrl}Emery.png`,
      },
      {
        name: "Jamal Musiala",
        position: "Mediocentro",
        overall: 86,
        precio: 20000000,
        url: `${storageBaseUrl}Musiala.png`,
      },
      {
        name: "Florian Wirtz",
        position: "Mediocentro",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Wirtz.png`,
      },
      {
        name: "Fabián Ruiz",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Ruiz.png`,
      },
      {
        name: "Alejandro Grimaldo",
        position: "Mediocentro",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}Grimaldo.png`,
      },
      {
        name: "Adrien Rabiot",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Rabiot.webp`,
      },
      {
        name: "Eduardo Camavinga",
        position: "Mediocentro",
        overall: 84,
        precio: 12000000,
        url: `${storageMadrid}Camavinga.png`,
      },
      {
        name: "Federico Valverde",
        position: "Mediocentro",
        overall: 87,
        precio: 20000000,
        url: `${storageMadrid}Valverde.png`,
      },
      {
        name: "Ismaël Bennacer",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}Bennacer.png`,
      },
      {
        name: "Nicolò Barella",
        position: "Mediocentro",
        overall: 86,
        precio: 20000000,
        url: `${storageBaseUrl}Barella.png`,
      },
      {
        name: "Luka Modrić",
        position: "Mediocentro",
        overall: 87,
        precio: 20000000,
        url: `${storageMadrid}Modric.webp`,
      },
      {
        name: "Joshua Kimmich",
        position: "Mediocentro",
        overall: 88,
        precio: 20000000,
        url: `${storageBaseUrl}Kimmich.png`,
      },
      {
        name: "Enzo Fernández",
        position: "Mediocentro",
        overall: 83,
        precio: 10000000,
        url: `${storageChelsea}Enzo.png`,
      },

      // Delanteros (16)
      {
        name: "Kylian Mbappé",
        position: "Delantero",
        overall: 91,
        precio: 25000000,
        url: `${storageMadrid}Kylian.png`,
      },
      {
        name: "Robert Lewandowski",
        position: "Delantero",
        overall: 90,
        precio: 22000000,
        url: `${storageBarcelona}Lewandowski.png`,
      },
      {
        name: "Lautaro Martínez",
        position: "Delantero",
        overall: 88,
        precio: 20000000,
        url: `${storageBaseUrl}Martínez.png`,
      },
      {
        name: "Victor Osimhen",
        position: "Delantero",
        overall: 88,
        precio: 20000000,
        url: `${storageBaseUrl}Osimhen.png`,
      },
      {
        name: "Mohamed Salah",
        position: "Delantero",
        overall: 89,
        precio: 22000000,
        url: `${storageLiverpool}Mohamed.png`,
      },
      {
        name: "Heung-min Son",
        position: "Delantero",
        overall: 87,
        precio: 20000000,
        url: `${storageBaseUrl}Son.png`,
      },
      {
        name: "Leroy Sané",
        position: "Delantero",
        overall: 85,
        precio: 15000000,
        url: `${storageBaseUrl}Sané.png`,
      },
      {
        name: "Randal Kolo Muani",
        position: "Delantero",
        overall: 84,
        precio: 12000000,
        url: `${storageBaseUrl}Muani.png`,
      },
      {
        name: "João Félix",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageChelsea}Félix.png`,
      },
      {
        name: "Rodrygo",
        position: "Delantero",
        overall: 85,
        precio: 15000000,
        url: `${storageMadrid}Rodrygo.webp`,
      },
      {
        name: "Ansu Fati",
        position: "Delantero",
        overall: 78,
        precio: 5000000,
        url: `${storageBarcelona}Ansu.png`,
      },
      {
        name: "Marcus Rashford",
        position: "Delantero",
        overall: 86,
        precio: 20000000,
        url: `${storageBaseUrl}Rashford.png`,
      },
      {
        name: "Neymar Jr",
        position: "Delantero",
        overall: 89,
        precio: 22000000,
        url: `${storageBaseUrl}Neymar.png`,
      },
      {
        name: "Karim Adeyemi",
        position: "Delantero",
        overall: 80,
        precio: 5000000,
        url: `${storageBaseUrl}Adeyemi.webp`,
      },
      {
        name: "Jonathan David",
        position: "Delantero",
        overall: 82,
        precio: 8000000,
        url: `${storageBaseUrl}David.png`,
      },
      {
        name: "Ángel Di María",
        position: "Delantero",
        overall: 83,
        precio: 10000000,
        url: `${storageBaseUrl}María.png`,
      },
    ];

    response.json({data: {players: mercadoPlayers}});
  }
);
