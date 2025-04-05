import fs from 'fs-extra';

const sourcePath = './node_modules/@pdftron/webviewer/public';
const destPath = './public/webviewer';

// Ensure destination directory exists
fs.ensureDirSync(destPath);

// Copy WebViewer files
try {
  fs.copySync(sourcePath, destPath, { overwrite: true });
  console.log('WebViewer files copied successfully');
} catch (err) {
  console.error('Error copying WebViewer files:', err);
  process.exit(1);
}
