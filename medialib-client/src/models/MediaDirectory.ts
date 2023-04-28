import AudioFile from "./AudioFile"

export default interface MediaDirectory {
    id: string
    path: string
    files: AudioFile[],
    subDirectories: MediaDirectory[]
}
