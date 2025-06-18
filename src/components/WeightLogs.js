import React, { useEffect, useState } from 'react';
import axios from 'axios';

const WeightLogs = () => {
  const [weightLogs, setWeightLogs] = useState([]);
  const [weight, setWeight] = useState('');
  const [targetWeight, setTargetWeight] = useState('');
  const [unit, setUnit] = useState('KG');
  const [userId, setUserId] = useState('');
  const [token, setToken] = useState('');

  const fetchWeightLogs = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/users/${userId}/weight-logs`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setWeightLogs(response.data.data);
    } catch (error) {
      console.error('Error fetching weight logs:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post(`http://localhost:8080/api/users/${userId}/weight-logs`, {
        weight: parseFloat(weight),
        targetWeight: parseFloat(targetWeight),
        timestamp: new Date().toISOString(),
        unit: unit
      }, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setWeight('');
      setTargetWeight('');
      setUnit('KG');
      fetchWeightLogs();
    } catch (error) {
      console.error('Error adding weight log:', error);
    }
  };

  useEffect(() => {
    fetchWeightLogs();
  }, []);

  return (
    <div>
      {/* Render your component content here */}
    </div>
  );
};

export default WeightLogs; 