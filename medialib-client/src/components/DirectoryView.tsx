import MediaDirectory from "../models/MediaDirectory"
import { FileList } from "./FileList";
import  * as Path  from "path"

//TODO: not currently rendering... (back to school on React + TS)
export default function DirectoryView() {

  //TODO: make this a param
  const rootPath = "C:\\some\\dir";

  const directory: MediaDirectory = { 
    id: "8b740efa-abc8-4e48-8160-0896159ea07f",
    path: rootPath,
    subDirectories: [],
    files: [
        {
            id: "9159a562-d256-4aad-a7b3-533269742ff6",
            filePath: Path.join(rootPath, "some-file.mp3"),
            fileName: "some-file.mp3"
        },
        {
            id: "114e08c9-0872-4205-b02f-78b3e72de827",
            filePath: Path.join(rootPath, "other-file.mp3"),
            fileName: "other-file.mp3"
        }
    ]
  };

  let fileList = FileList(directory.files);
  return (
    <div>
      <p>DO YOU SEE THIS?</p>
      <h3>{directory.path}</h3>
      <ul>
        {fileList}
      </ul>
    </div>
  )
}