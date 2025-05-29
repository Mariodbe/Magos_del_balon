import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";

import {getBarcelonaPlayers} from "./equipos/barcelona";
import {getMadridPlayers} from "./equipos/realMadrid";
import {getAtleticoPlayers} from "./equipos/atletico";
import {getManCityPlayers} from "./equipos/manCity";
import {getLiverpoolPlayers} from "./equipos/liverpool";
import {getChelseaPlayers} from "./equipos/chelsea";
import {getArsenalPlayers} from "./equipos/arsenal";
import {getAthleticClubPlayers} from "./equipos/athletic";
import {getMercadoPlayers} from "./mercado/mercado";

admin.initializeApp();

export {
  getBarcelonaPlayers,
  getMadridPlayers,
  getAtleticoPlayers,
  getManCityPlayers,
  getLiverpoolPlayers,
  getChelseaPlayers,
  getArsenalPlayers,
  getAthleticClubPlayers,
  getMercadoPlayers,
};

export const enviarNotificacion = onDocumentCreated("chats/{chatId}/mensajes/{mensajeId}",
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      console.error("No data in event");
      return;
    }
    const mensaje = snapshot.data();
    const destinatarioId = mensaje.destinatarioId;
    const remitenteNombre = mensaje.remitenteNombre || "Alguien";

    try {
      const usuarioDoc = await admin.firestore().collection("users").doc(destinatarioId).get();
      const userData = usuarioDoc.data();
      if (!userData) return;

      const token = userData.fcmToken;

      if (token) {
        const payload = {
          notification: {
            title: "Nuevo mensaje",
            body: `Tienes un nuevo mensaje de ${remitenteNombre}`,
          },
          token,
        };

        await admin.messaging().send(payload);
        console.log(`Notificación enviada a ${destinatarioId}`);
      }
    } catch (error) {
      console.error("Error al enviar la notificación:", error);
    }
  });
