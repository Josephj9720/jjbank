import { Button, Card, CardContent, TextField } from "@mui/material";
import useTitle from "../../hooks/useTitle";
import { useState } from "react";
import api from "../../util/apiClient";
import { API_ENDPOINTS } from "../../util/endpoints";
import { FRONT_END_ROUTES } from "../../util/routes";
import { useNavigate } from "react-router";

const Login = () => {
  useTitle("Login | JJ Bank");

  const [formData, setFormData] = useState({
    email: "",
    passowrd: "",
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({...formData, [e.target.name]: e.target.value});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post(API_ENDPOINTS.LOGIN, formData);
      console.log("success", response.data);
      navigate(FRONT_END_ROUTES.DASHBOARD);
    } catch (error) {
      console.log("error submitting form", error);
    }

  };

  return (
    <Card>
      <CardContent>
        <form noValidate autoComplete="off" onSubmit={handleSubmit}>
          <TextField
            label="Email"
            variant="outlined"
            color="primary"
            required
            name="email"
            value={formData.email}
            onChange={handleChange}
          />
          <TextField
            label="Password"
            variant="outlined"
            color="primary"
            required
            name="password"
            value={formData.passoword}
            onChange={handleChange}
          />
          <Button 
            type="submit"
            variant="contained"
            color="secondary"
          >
            Submit
          </Button>
        </form>
      </CardContent>
    </Card>
  );
}

export default Login;