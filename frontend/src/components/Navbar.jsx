import { Box, AppBar, Toolbar, IconButton } from '@mui/material'

export default function Navbar() {
  return (
      
      <Box sx={{flexGrow: 1}}>
        <AppBar position='fixed'>
          <Toolbar>
            
          </Toolbar>
        </AppBar>
        <Toolbar /> {/* for spacing, so that content start below main toolbar */}
      </Box>

  );
};