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
        position: "Goalkeeper",
        overall: 89,
        precio: 7387000,
        url: `${storageBarcelona}TerStegen.png`,
      },
      {
        name: "Alisson Becker",
        position: "Goalkeeper",
        overall: 89,
        precio: 7387000,
        url: `${storageLiverpool}Alisson.png`,
      },
      {
        name: "Mike Maignan",
        position: "Goalkeeper",
        overall: 87,
        precio: 7221000,
        url: `${storageBaseUrl}Maignan.png`,
      },
      {
        name: "Jan Oblak",
        position: "Goalkeeper",
        overall: 88,
        precio: 7304000,
        url: `${storageAtletico}OBLAK.png`,
      },
      {
        name: "Gregor Kobel",
        position: "Goalkeeper",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Kobel.webp`,
      },
      {
        name: "Gianluigi Donnarumma",
        position: "Goalkeeper",
        overall: 87,
        precio: 7221000,
        url: `${storageBaseUrl}Donnarumma.png`,
      },

      // Defensores (14)
      {
        name: "Ronald Araújo",
        position: "Defender",
        overall: 86,
        precio: 7138000,
        url: `${storageBarcelona}araujo.png`,
      },
      {
        name: "William Saliba",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Saliba.png`,
      },
      {
        name: "Theo Hernández",
        position: "Defender",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Hernández.png`,
      },
      {
        name: "Achraf Hakimi",
        position: "Defender",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Hakimi.png`,
      },
      {
        name: "Alphonso Davies",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Davies.png`,
      },
      {
        name: "Dayot Upamecano",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Upamecano.png`,
      },
      {
        name: "Jules Koundé",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBarcelona}Kounde.png`,
      },
      {
        name: "Giovanni Di Lorenzo",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Lorenzo.webp`,
      },
      {
        name: "Jeremie Frimpong",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Frimpong.png`,
      },
      {
        name: "Antonio Rüdiger",
        position: "Defender",
        overall: 86,
        precio: 7138000,
        url: `${storageMadrid}Rudiger.png`,
      },
      {
        name: "Raphaël Varane",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Varane.png`,
      },
      {
        name: "Matthijs de Ligt",
        position: "Defender",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Ligt.png`,
      },
      {
        name: "Milan Škriniar",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Škriniar.png`,
      },
      {
        name: "Benjamin Pavard",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Pavard.png`,
      },

      // Centrocampistas (16)
      {
        name: "Frenkie de Jong",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
        url: `${storageBarcelona}DeJong.png`,
      },
      {
        name: "Martin Ødegaard",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
        url: `${storageArsenal}Ødegaard.png`,
      },
      {
        name: "Sandro Tonali",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Tonali.png`,
      },
      {
        name: "Warren Zaïre-Emery",
        position: "Midfielder",
        overall: 78,
        precio: 6474000,
        url: `${storageBaseUrl}Emery.png`,
      },
      {
        name: "Jamal Musiala",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
        url: `${storageBaseUrl}Musiala.png`,
      },
      {
        name: "Florian Wirtz",
        position: "Midfielder",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Wirtz.png`,
      },
      {
        name: "Fabián Ruiz",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Ruiz.png`,
      },
      {
        name: "Alejandro Grimaldo",
        position: "Midfielder",
        overall: 82,
        precio: 6806000,
        url: `${storageBaseUrl}Grimaldo.png`,
      },
      {
        name: "Adrien Rabiot",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Rabiot.webp`,
      },
      {
        name: "Eduardo Camavinga",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
        url: `${storageMadrid}Camavinga.png`,
      },
      {
        name: "Federico Valverde",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
        url: `${storageMadrid}Valverde.png`,
      },
      {
        name: "Ismaël Bennacer",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Bennacer.png`,
      },
      {
        name: "Nicolò Barella",
        position: "Midfielder",
        overall: 86,
        precio: 7138000,
        url: `${storageBaseUrl}Barella.png`,
      },
      {
        name: "Luka Modrić",
        position: "Midfielder",
        overall: 87,
        precio: 7221000,
        url: `${storageMadrid}Modric.webp`,
      },
      {
        name: "Joshua Kimmich",
        position: "Midfielder",
        overall: 88,
        precio: 7304000,
        url: `${storageBaseUrl}Kimmich.png`,
      },
      {
        name: "Enzo Fernández",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageChelsea}Enzo.png`,
      },

      // Delanteros (16)
      {
        name: "Kylian Mbappé",
        position: "Forward",
        overall: 91,
        precio: 7553000,
        url: `${storageMadrid}Kylian.png`,
      },
      {
        name: "Robert Lewandowski",
        position: "Forward",
        overall: 90,
        precio: 7470000,
        url: `${storageBarcelona}Lewandowski.png`,
      },
      {
        name: "Lautaro Martínez",
        position: "Forward",
        overall: 88,
        precio: 7304000,
        url: `${storageBaseUrl}Martínez.png`,
      },
      {
        name: "Victor Osimhen",
        position: "Forward",
        overall: 88,
        precio: 7304000,
        url: `${storageBaseUrl}Osimhen.png`,
      },
      {
        name: "Mohamed Salah",
        position: "Forward",
        overall: 89,
        precio: 7387000,
        url: `${storageLiverpool}Mohamed.png`,
      },
      {
        name: "Heung-min Son",
        position: "Forward",
        overall: 87,
        precio: 7221000,
        url: `${storageBaseUrl}Son.png`,
      },
      {name: "Leroy Sané",
        position: "Forward",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Sané.png`,
      },
      {
        name: "Randal Kolo Muani",
        position: "Forward",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Muani.png`,
      },
      {
        name: "João Félix",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageChelsea}Félix.png`,
      },
      {
        name: "Rodrygo",
        position: "Forward",
        overall: 85,
        precio: 7055000,
        url: `${storageMadrid}Rodrygo.webp`,
      },
      {
        name: "Ansu Fati",
        position: "Forward",
        overall: 78,
        precio: 6474000,
        url: `${storageBarcelona}Ansu.png`,
      },
      {
        name: "Marcus Rashford",
        position: "Forward",
        overall: 86,
        precio: 7138000,
        url: `${storageBaseUrl}Rashford.png`,
      },
      {
        name: "Neymar Jr",
        position: "Forward",
        overall: 89,
        precio: 7387000,
        url: `${storageBaseUrl}Neymar.png`,
      },
      {
        name: "Karim Adeyemi",
        position: "Forward",
        overall: 80,
        precio: 6640000,
        url: `${storageBaseUrl}Adeyemi.webp`,
      },
      {
        name: "Jonathan David",
        position: "Forward",
        overall: 82,
        precio: 6806000,
        url: `${storageBaseUrl}David.png`,
      },
      {
        name: "Ángel Di María",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}María.png`,
      },
    ];

    response.json({data: {players: mercadoPlayers}});
  }
);
