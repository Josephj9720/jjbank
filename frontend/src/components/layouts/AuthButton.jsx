import { alpha, Button, darken } from '@mui/material';

const AuthButton = ({ type = "submit", text, variant = "contained", onClick }) => {
  return (
    <Button
      type={type}
      variant={variant}
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
        "backgroundColor" : (theme) => theme.palette.authButton[variant].background,
        "color" : (theme) => theme.palette.authButton[variant].text,
        ":hover" : {
          "backgroundColor" : (theme) => variant === "contained" ? darken(theme.palette.authButton.contained.background, 0.16) : alpha(theme.palette.authButton.outlined.ripple, 0.08),
        },
        ".MuiTouchRipple-child" : {
          backgroundColor: (theme) => theme.palette.authButton[variant].ripple,
        }
      }}
    >
      {text}
    </Button>
  );
}

export default AuthButton;