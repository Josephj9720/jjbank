import { Button, Card, CardContent, TextField, Typography } from "@mui/material";
import useTitle from "../../hooks/useTitle";
import { useState } from "react";
import api from "../../util/apiClient";
import { API_ENDPOINTS } from "../../util/endpoints";
import { FRONT_END_ROUTES } from "../../util/routes";
import { useNavigate } from "react-router";
import AuthCard from "../AuthCard";
import AuthButton from "../AuthButton";
import { validateLogin } from "../../util/authValidator";

const Login = () => {
  useTitle("Login | JJ Bank");

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [formErrors, setFormErrors] = useState({
    email: "",
    password: "",
  })

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({...formData, [e.target.name]: e.target.value});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const {isValid, emailErrorMessage, passwordErrorMessage} = validateLogin(formData.email, formData.password);
    if(isValid) {
      try {
        const response = await api.post(API_ENDPOINTS.LOGIN, formData);
        console.log("success", response.data);
        navigate(FRONT_END_ROUTES.DASHBOARD);
      } catch (error) {
        console.log("error submitting form", error);
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