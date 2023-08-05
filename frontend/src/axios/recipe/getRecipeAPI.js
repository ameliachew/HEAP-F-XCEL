import Cookies from "js-cookie";
import { protectedInstance as instance } from "../instance";

const getRecipe = async ({ params: { id } }) => {
    try {
        const result = await instance.get(`/user/findRecipe/${id}`, {
            headers: { Authorization: `Bearer ${Cookies.get("token")}` },
            // headers: {
            //     Authorization: `Bearer ${"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmljbmd5b25nd2VpQGdtYWlsLmNvbSIsImlhdCI6MTY4OTA0MDQyMSwiZXhwIjoxNjg5MTI2ODIxfQ.dq4Xgx8AColJm86n4vJPOvzhVQs221XxoaHvbW74q1w"}`,
            // },
        });

        return result.data;
    } catch (e) {
        let msg = "";
        if (!e.response) {
            msg =
                "Get recipe unsuccessful due to network error!\nPlease check your internet connection!";
        } else {
            const { error, message } = e.response.data;
            msg = error + "\n" + message;
        }

        throw msg;
    }
};

export default getRecipe;
