import * as functions from "firebase-functions";

export const getLiverpoolPlayers = functions.https.onRequest(
  (request, response) => {
    const storageBaseUrl = "gs://magosdelbalon-f8f08."+
    "firebasestorage.app/Liverpool/";
    const liverpoolPlayers = [
      {
        name: "Alisson Becker",
        position: "Goalkeeper",
        overall: 89,
        precio: 7387000,
        url: `${storageBaseUrl}Alisson.png`,
      },
      {
        name: "Caoimhín Kelleher",
        position: "Goalkeeper",
        overall: 78,
        precio: 6474000,
        url: `${storageBaseUrl}Kelleher.png`,
      },
      {
        name: "Virgil van Dijk",
        position: "Defender",
        overall: 89,
        precio: 7387000,
        url: `${storageBaseUrl}Virgil.png`,
      },
      {
        name: "Ibrahima Konaté",
        position: "Defender",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Konaté.png`,
      },
      {
        name: "Joe Gomez",
        position: "Defender",
        overall: 80,
        precio: 6640000,
        url: `${storageBaseUrl}Gomez.png`,
      },
      {
        name: "Andy Robertson",
        position: "Defender",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Robertson.png`,
      },
      {
        name: "Trent Alexander-Arnold",
        position: "Defender",
        overall: 87,
        precio: 7221000,
        url: `${storageBaseUrl}Alexander.png`,
      },
      {
        name: "Kostas Tsimikas",
        position: "Defender",
        overall: 77,
        precio: 6381000,
        url: `${storageBaseUrl}Tsimikas.png`,
      },
      {
        name: "Jarell Quansah",
        position: "Defender",
        overall: 70,
        precio: 5810000,
        url: `${storageBaseUrl}Quansah.png`,
      },
      {
        name: "Conor Bradley",
        position: "Defender",
        overall: 72,
        precio: 5976000,
        url: `${storageBaseUrl}Bradley.png`,
      },
      {
        name: "Wataru Endo",
        position: "Midfielder",
        overall: 81,
        precio: 6723000,
        url: `${storageBaseUrl}Wataru.png`,
      },
      {
        name: "Alexis Mac Allister",
        position: "Midfielder",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Allister.png`,
      },
      {
        name: "Dominik Szoboszlai",
        position: "Midfielder",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Szoboszlai.png`,
      },
      {
        name: "Curtis Jones",
        position: "Midfielder",
        overall: 78,
        precio: 6474000,
        url: `${storageBaseUrl}Curtis.png`,
      },
      {
        name: "Harvey Elliott",
        position: "Midfielder",
        overall: 77,
        precio: 6381000,
        url: `${storageBaseUrl}Elliott.png`,
      },
      {
        name: "Ryan Gravenberch",
        position: "Midfielder",
        overall: 80,
        precio: 6640000,
        url: `${storageBaseUrl}Gravenberch.png`,
      },
      {
        name: "Tyler Morton",
        position: "Midfielder",
        overall: 72,
        precio: 5644000,
        url: `${storageBaseUrl}Morton.png`,
      },
      {
        name: "Mohamed Salah",
        position: "Forward",
        overall: 90,
        precio: 7470000,
        url: `${storageBaseUrl}Mohamed.png`,
      },
      {
        name: "Luis Díaz",
        position: "Forward",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Luis.png`,
      },
      {
        name: "Darwin Núñez",
        position: "Forward",
        overall: 84,
        precio: 6972000,
        url: `${storageBaseUrl}Darwin.png`,
      },
      {
        name: "Diogo Jota",
        position: "Forward",
        overall: 83,
        precio: 6889000,
        url: `${storageBaseUrl}Diogo.png`,
      },
      {
        name: "Cody Gakpo",
        position: "Forward",
        overall: 82,
        precio: 6806000,
        url: `${storageBaseUrl}Gakpo.png`,
      },
      {
        name: "Federico Chiesa",
        position: "Forward",
        overall: 85,
        precio: 7055000,
        url: `${storageBaseUrl}Chiesa.png`,
      },
    ];
    const totalOverall = liverpoolPlayers.reduce((acc, player) => acc + player.overall, 0);
    const averageOverall = totalOverall / liverpoolPlayers.length;

    response.json({data: {players: liverpoolPlayers, averageOverall}});
  }
);
