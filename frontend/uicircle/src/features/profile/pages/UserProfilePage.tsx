import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "@/api/axios";

interface Profile {
  id: string;
  name: string;
  email: string;
  bio?: string;
  avatarUrl?: string;
}

export default function UserProfilePage() {
  const { id } = useParams();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!id) return;
    
    axios
      .get(`/profile/${id}`)
      .then((res) => setProfile(res.data))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div>Loading profile…</div>;

  if (!profile)
    return <div>User not found.</div>;

  return (
    <div style={{ padding: "2rem" }}>
      <h1>{profile.name}</h1>
      <p>{profile.email}</p>

      {profile.bio && <p>{profile.bio}</p>}
    </div>
  );
}
