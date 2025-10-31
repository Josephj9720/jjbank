import { Box } from "@mui/material";

const SectionRow = ({ children }) => {

  return (
    <Box
      sx={{
        "display": "flex",
        "flexDirection": "row",
        "width": "100%",
      }}
    >
      {children}
    </Box>
  );

};

export default SectionRow;