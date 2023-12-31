import Cookies from "js-cookie";
import { protectedInstance as instance } from "../instance";
import handleBusinessField from "../../functions/handleBusinessField";

const updateBusiness = async (data, businessId) => {
    try {
        const payload = await handleBusinessField(data);
        const result = await instance.put(
            `/user/updateBGP/${businessId}`,
            payload,
            {
                headers: { Authorization: `Bearer ${Cookies.get("token")}` },
                // headers: {
                //     Authorization: `Bearer ${"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlcmljbmd5b25nd2VpQGdtYWlsLmNvbSIsImlhdCI6MTY4OTA0MDQyMSwiZXhwIjoxNjg5MTI2ODIxfQ.dq4Xgx8AColJm86n4vJPOvzhVQs221XxoaHvbW74q1w"}`,
                // },
            }
        );
        return result.data.response;
    } catch (e) {
        let msg = "";
        if (!e.response) {
            msg =
                "Update business growth plan unsuccessful due to network error!\nPlease check your internet connection!";
        } else {
            const { error, message } = e.response.data;
            msg = error + "\n" + message;
        }

        throw msg;
    }
};

export default updateBusiness;
