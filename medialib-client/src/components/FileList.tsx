import type AudioFile from '../models/AudioFile';

function FileView(file: AudioFile) {
  return (
      <li key={file.id}>{file.fileName}</li>
  )
}

export function FileList(files: AudioFile[]) {
  let fileEntries = files.map(f => FileView(f));
  const noFiles = ( <em>No files found</em>);
  return (
    <>
      { fileEntries.length ? fileEntries : noFiles }
    </>
  )
}