import { Box, CardContent, TextField, Typography } from "@mui/material";
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
    email: "",
    password: "",
  });

  const [formErrors, setFormErrors] = useState({
    email: "",
    password: "",
  });

  const [failedLogin, setFailedLogin] = useState(false);

  const auth = useAuthContext();

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({...formData, [e.target.name]: e.target.value});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const { isValid, emailErrorMessage, passwordErrorMessage } = validateLogin(formData.email, formData.password);
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
        setFailedLogin(true);
      }
    } else {
      setFormErrors({
        email: emailErrorMessage,
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
        Login using your email
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
            You have entered incorrect information. Please verify your info and try again.
          </Typography>
        </Box> }
        <form noValidate autoComplete="off" onSubmit={handleSubmit}>
          <TextField
            error={!!formErrors.email}
            fullWidth
            label="Email"
            variant="outlined"
            color="primary"
            required
            name="email"
            value={formData.email}
            onChange={handleChange}
            sx={{
              "marginBottom" : "3%"
            }}
            helperText={formErrors.email}
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