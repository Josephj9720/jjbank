import { Button } from '@mui/material';

const AuthButton = ({ type, text, variant, onClick }) => {
  return (
    <Button
      type={type ? type : "submit"}
      color="secondary"
      variant={variant ? variant : "contained"}
      onClick={onClick}
      size="large"
      sx={{
        "width": {
          "xs" : "100%",
          "sm" : "55%",
          "md" : "45%",
          "lg" : "40%",
        },
        "margin" : {
          "xs" : "0 0 3%",
          "md" : "0 1.5%",
        },
        "padding" : "0.80rem",
      }}
    >
      {text}
    </Button>
  );
}

export default AuthButton;