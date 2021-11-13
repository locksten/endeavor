export const getLastMidnight = () => {
  const midnight = new Date()
  midnight.setHours(0, 0, 0, 0)
  return midnight
}

export const isObjectEmpty = (obj) => {
  for (const _ in obj) return false
  return true
}
