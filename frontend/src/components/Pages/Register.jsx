import { Button, Card, CardContent, TextField } from "@mui/material";
import api from "../../util/apiClient";
import { useState } from "react";
import { useNavigate } from "react-router";
import { API_ENDPOINTS } from "../../util/endpoints"
import { FRONT_END_ROUTES } from "../../util/routes";
import useTitle from "../../hooks/useTitle";

const Register = () => {
  useTitle("Register | JJ Bank")
  const [formData, setFormData] = useState({
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

    try {
      const response = await api.post(API_ENDPOINTS.AUTH + "/register", formData);
      console.log("success:", response.data);
      navigate(FRONT_END_ROUTES.LOGIN);

    } catch(error) {
      console.log("error submitting form: ", error);
    }
  };


  return (
    <Card>
      <CardContent>
        <form noValidate autoComplete="off" onSubmit={handleSubmit}>
          <TextField
            label="Full Name"
            variant="outlined"
            color="primary"
            name="fullName"
            value={formData.fullName}
            onChange={handleChange}
          />
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
            value={formData.password}
            onChange={handleChange}
          />
          <Button
          type="submit"
          color="secondary"
          variant="contained"
        >
          Submit
        </Button>
        </form>
      </CardContent>
    </Card>
  );
}

export default Register;