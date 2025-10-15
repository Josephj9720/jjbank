import { CardContent, TextField, Typography, Select, MenuItem } from "@mui/material";
import AuthCard from "../layouts/AuthCard";
import api from "../../util/apiClient";
import { useState } from "react";
import { useNavigate } from "react-router";
import { API_ENDPOINTS } from "../../util/endpoints"
import { FRONT_END_ROUTES } from "../../util/routes";
import useTitle from "../../hooks/useTitle";
import AuthButton from "../layouts/AuthButton";
import { validateRegister } from "../../util/authValidator";

const Register = () => {
  useTitle("Register | JJ Bank")
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    password: "",
    accountType: "checking",
  });

  const [formErrors, setFormErrors] = useState({
    fullName: "",
    email: "",
    password: "",
  });

  const navigate = useNavigate(); //Hook for navigation

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const { 
      isValid, 
      nameErrorMessage, 
      emailErrorMessage, 
      passwordErrorMessage } = validateRegister(formData.fullName, formData.email, formData.password);

    if(isValid) {
      try {
        const response = await api.post(API_ENDPOINTS.REGISTER, formData);
        console.log("success:", response.data);
        navigate(FRONT_END_ROUTES.LOGIN);

      } catch(error) {
        console.log("error submitting form: ", error);
      }
    } else {
      setFormErrors({
        fullName: nameErrorMessage,
        email: emailErrorMessage,
        password: passwordErrorMessage,
      });
    }
  };


  return (

      <AuthCard>
        <CardContent>
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
            Register today
          </Typography>
          <Typography 
            variant="h5"
            marginBottom={"5%"}
          >
            Securely manage your finances
          </Typography>
          <form noValidate autoComplete="off" onSubmit={handleSubmit}>
            <TextField
              error={!!formErrors.fullName}
              fullWidth
              label="Full Name"
              variant="outlined"
              color="primary"
              required
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              sx={{
                "marginBottom" : "3%",
              }}
              helperText={formErrors.fullName}
            />
            <TextField
              slotProps={{
                htmlInput: {
                  autoComplete: "off"
                }
              }}
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
                "marginBottom" : "3%",
              }}
              helperText={formErrors.email}
            />
            <TextField
              slotProps={{
                htmlInput: {
                  autoComplete: "new-password"
                }
              }}
              error={!!formErrors.password}
              fullWidth
              type="password"
              label="Password"
              variant="outlined"
              color="primary"
              required
              name="password"
              value={formData.password}
              onChange={handleChange}
              sx={{
                "marginBottom" : "3%",
              }}
              helperText={formErrors.password}
            />
            <TextField
              select
              fullWidth
              name="accountType"
              label="Bank Account Type"
              color="primary"
              value={formData.accountType}
              onChange={handleChange}
              required
              sx={{
                "marginBottom" : "5%",
              }}
            >
              <MenuItem value="checking">Checking</MenuItem>
              <MenuItem value="savings">Savings</MenuItem>
            </TextField>
            <AuthButton text={"Confirm"}/>
          </form>
        </CardContent>
      </AuthCard>
  );
}

export default Register;