import { Box, CardContent, imageListClasses, TextField, Typography } from "@mui/material";
import useTitle from "../../hooks/useTitle";
import { useState } from "react";
import api from "../../util/apiClient";
import { API_ENDPOINTS } from "../../util/endpoints";
import { FRONT_END_ROUTES } from "../../util/routes";
import { useNavigate } from "react-router";
import AuthCard from "../layouts/AuthCard";
import AuthButton from "../layouts/AuthButton";
import { validateLogin } from "../../util/authValidator";
import { useAuthContext } from "../../hooks/useAuthentication";

const Login = () => {
  useTitle("Login | JJ Bank");

  const [formData, setFormData] = useState({
    identifier: "",
    password: "",
  });

  const [formErrors, setFormErrors] = useState({
    identifier: "",
    password: "",
  });

  const [failedLogin, setFailedLogin] = useState(false);

  const [loginErrorMessage, setLoginErrorMessage] = useState(

  );

  const auth = useAuthContext();

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({...formData, [e.target.name]: e.target.value});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const { isValid, identifierErrorMessage, passwordErrorMessage } = validateLogin(formData.identifier, formData.password);
    if(isValid) {
      try {
        const response = await api.post(API_ENDPOINTS.LOGIN, formData, {
          withCredentials: true //this allows to receive the cookie
        });
        console.log("success", response.data);
        auth.setAuthDetails({
          fullName: response.data.fullName,
          accessToken: response.data.accessToken, 
        });
        
      } catch (error) {
        console.log("error submitting form", error);
        console.log("status code: ", error.response.status);
        if(error.response.status === 500) {
          setLoginErrorMessage("Something went wrong! Please try again later. ");
        } else {
          setLoginErrorMessage("You have entered incorrect information. Please verify your info and try again.");
        }
        setFailedLogin(true);
        
      }
    } else {
      setFormErrors({
        identifier: identifierErrorMessage,
        password: passwordErrorMessage,
      });
    }

  };

  const handleRegister = () => {
    navigate(FRONT_END_ROUTES.REGISTER);
  }

  return (
    <AuthCard>
      <Typography 
        variant="h6"
        marginBottom={"1%"}
      >
        JJ BANK ONLINE BANKING
      </Typography>
      <Typography 
        variant="h4"
        marginBottom={"1%"}
      >
        Log in to your JJB account
      </Typography>
      <Typography 
        variant="h5"
        marginBottom={"5%"}
      >
        Securely manage your finances
      </Typography>
      <CardContent>
        { failedLogin && <Box
          sx={{
            marginBottom: "3%",
            border: "2px solid #FF474C",
            borderRadius: "15px",
            backgroundColor: "#FF999C",
            padding: "1.5%"
          }}
        >
          <Typography variant="body1">
            {loginErrorMessage}
          </Typography>
        </Box> }
        <form noValidate autoComplete="off" onSubmit={handleSubmit}>
          <TextField
            error={!!formErrors.identifier}
            fullWidth
            label="Email or Access Card"
            variant="outlined"
            color="primary"
            required
            name="identifier"
            value={formData.identifier}
            onChange={handleChange}
            sx={{
              "marginBottom" : "3%"
            }}
            helperText={formErrors.identifier}
          />
          <TextField
            error={!!formErrors.password}
            type="password"
            fullWidth
            label="Password"
            variant="outlined"
            color="primary"
            required
            name="password"
            value={formData.password}
            onChange={handleChange}
            sx={{
              "marginBottom" : "5%"
            }}
            helperText={formErrors.password}
          />
          <AuthButton 
            type={"button"}
            variant={"outlined"}
            text={"Register now"}
            onClick={handleRegister}
          />
          <AuthButton 
            text={"Login"}
          />
        </form>
      </CardContent>
    </AuthCard>
  );
}

export default Login;