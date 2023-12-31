import HeadingOne from "../../components/common/heading/HeadingOne";
import RegisterIllustration from "../../assets/illustrations/user/registration-illustration-2.jpg";
import { Fragment, useEffect } from "react";
import Grid from "@mui/material/Unstable_Grid2/Grid2";
import TextField from "../../components/common/form/TextField";
import HeadingTwo from "../../components/common/heading/HeadingTwo";
import { businessType, cuisineType } from "../../constants/dropdown-choices";
import Checkbox from "../../components/common/form/Checkbox";
import FieldsColumn from "../../components/common/form/FieldsColumn";
import TextArea from "../../components/common/form/TextArea";
import SingleItemDropdown from "../../components/common/form/SingleItemDropdown";
import SubmitFormGroup from "../../components/common/form/SubmitFormGroup";
import { FormProvider, useForm } from "react-hook-form";
import { useNavigate, useLocation } from "react-router-dom";
import MultiItemDropdown from "../../components/common/form/MultiItemDropdown";
import register from "../../axios/auth/registerAPI";
import login from "../../axios/auth/loginAPI";
import { queueError } from "../../functions/formHandling";
import { enqueueSnackbar } from "notistack";
import Cookies from "js-cookie";

const RegistrationTwo = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const formMethods = useForm({ defaultValues: { email: "", password: "" } });
    const { setValue, watch } = formMethods;
    const watchEmail = watch("email");
    const watchPassword = watch("password");

    useEffect(() => {
        if (location.state) {
            const { email, password } = location.state;

            if (email && password) {
                setValue("email", email);
                setValue("password", password);
            }
        } else if (watchEmail === "" && watchPassword === "") {
            return navigate("/register", {
                state: {
                    info: "You have yet to fill your email and password for registration!",
                },
            });
        }
    }, [location.state, setValue, navigate, watchPassword, watchEmail]);

    return (
        <Fragment>
            <HeadingOne divider={true}>Register: Business Details</HeadingOne>
            <FormProvider {...formMethods}>
                <Grid container spacing={2}>
                    <Grid xs={6}>
                        <HeadingTwo>Basic Details</HeadingTwo>
                        <FieldsColumn>
                            <TextField
                                rules={{ required: true }}
                                name="businessName"
                                label="Name of F&B Business"
                                size="large"
                            />
                            <TextArea
                                rules={{ required: true }}
                                name="storeAddress"
                                label="Store Address"
                            />
                            <TextField
                                rules={{ required: true }}
                                name="postalCode"
                                label="Postal Code"
                                type="number"
                                size="large"
                            />
                        </FieldsColumn>
                        <HeadingTwo>Cuisine Choices</HeadingTwo>
                        <FieldsColumn>
                            <SingleItemDropdown
                                name="businessType"
                                rules={{ required: true }}
                                label="Type of F&B Business"
                                size="large"
                                choices={businessType}
                            />
                            <MultiItemDropdown
                                name="cuisineType"
                                rules={{ required: true }}
                                label="Type of Cuisine"
                                size="large"
                                choices={cuisineType}
                            />
                            <Checkbox name="isFusion" label="Fusion Cuisine?" />
                        </FieldsColumn>
                    </Grid>
                    <Grid xs={6}>
                        <img
                            style={{ width: "100%" }}
                            src={RegisterIllustration}
                            alt="Register Illustration"
                        />
                    </Grid>
                </Grid>
                <SubmitFormGroup
                    submitErrorText="Registration unsuccessful, please check your input"
                    onSubmit={async (data) => {
                        try {
                            await register(data);
                            const token = await login(data);
                            await Cookies.set("token", token);
                            navigate("/my-summary");
                        } catch (e) {
                            queueError(e, enqueueSnackbar);
                        }
                    }}
                    submitText="Register Now"
                />
            </FormProvider>
        </Fragment>
    );
};

export default RegistrationTwo;
