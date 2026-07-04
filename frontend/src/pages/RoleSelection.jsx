import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../services/api';
import { Briefcase, Target, ShieldAlert, Sparkles, CheckCircle2, ChevronRight } from 'lucide-react';

export const RoleSelection = () => {
  const navigate = useNavigate();
  const [roles, setRoles] = useState([]);
  const [selectedRoleId, setSelectedRoleId] = useState(null);
  const [loadingRoles, setLoadingRoles] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);
  const [analysisStatus, setAnalysisStatus] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const data = await apiService.getRoles();
        setRoles(data);
      } catch (err) {
        setError('Failed to fetch job roles. Please check connection.');
      } finally {
        setLoadingRoles(false);
      }
    };
    fetchRoles();
  }, []);

  const handleRunAnalysis = async () => {
    if (!selectedRoleId) return;
    setError('');
    setAnalyzing(true);
    
    // Simulate multi-stage visual loader progress
    const steps = [
      'Extracting skills profile...',
      'Matching against database reference standards...',
      'Computing deterministic skill score...',
      'Querying Qdrant vector database for resource matches...',
      'Generating custom roadmap using Gemini...',
      'Caching analysis results...'
    ];

    let currentStep = 0;
    setAnalysisStatus(steps[0]);

    const statusInterval = setInterval(() => {
      if (currentStep < steps.length - 1) {
        currentStep++;
        setAnalysisStatus(steps[currentStep]);
      }
    }, 1500);

    try {
      await apiService.runAnalysis(selectedRoleId);
      clearInterval(statusInterval);
      setAnalysisStatus('Analysis complete! Redirecting...');
      setTimeout(() => {
        navigate('/');
      }, 1000);
    } catch (err) {
      clearInterval(statusInterval);
      setError(err.response?.data?.error || 'Failed to complete skill gap analysis. Make sure you uploaded a resume.');
      setAnalyzing(false);
    }
  };

  if (loadingRoles) {
    return (
      <div className="min-h-screen bg-slate-950 flex flex-col items-center justify-center text-slate-100">
        <div className="w-12 h-12 border-4 border-emerald-500/20 border-t-emerald-500 rounded-full animate-spin"></div>
        <p className="mt-4 text-slate-400 text-sm">Fetching job roles...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 flex flex-col items-center justify-center relative overflow-hidden">
      {/* Background radial glow */}
      <div className="absolute top-1/4 left-1/4 w-[400px] h-[400px] bg-emerald-500/5 rounded-full blur-3xl"></div>
      <div className="absolute bottom-1/4 right-1/4 w-[400px] h-[400px] bg-cyan-500/5 rounded-full blur-3xl"></div>

      <div className="w-full max-w-4xl relative z-10">
        {/* Loading Overlay */}
        {analyzing && (
          <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-md z-50 flex flex-col items-center justify-center">
            <div className="w-20 h-20 relative">
              <div className="absolute inset-0 rounded-full border-4 border-emerald-500/20"></div>
              <div className="absolute inset-0 rounded-full border-4 border-t-emerald-500 animate-spin"></div>
            </div>
            <h2 className="mt-6 text-xl font-bold text-white tracking-wide">
              Analyzing Skill Gap
            </h2>
            <div className="mt-2 text-emerald-400 font-medium text-sm flex items-center gap-2">
              <Sparkles size={16} className="animate-pulse" />
              <span>{analysisStatus}</span>
            </div>
          </div>
        )}

        <div className="text-center mb-10">
          <h1 className="text-4xl font-extrabold tracking-tight text-white mb-2">
            Select Your Target Job Role
          </h1>
          <p className="text-slate-400 text-base">
            Compare your resume skills against standardized corporate requirements.
          </p>
        </div>

        {error && (
          <div className="flex items-center gap-3 text-rose-400 bg-rose-500/10 border border-rose-500/20 p-4 rounded-xl text-sm mb-6 max-w-2xl mx-auto">
            <ShieldAlert size={20} className="shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {/* Roles Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
          {roles.map((role) => {
            const isSelected = selectedRoleId === role.id;
            return (
              <div
                key={role.id}
                onClick={() => setSelectedRoleId(role.id)}
                className={`glass rounded-2xl p-6 cursor-pointer border transition-all duration-300 relative overflow-hidden group ${
                  isSelected
                    ? 'border-emerald-500 bg-emerald-500/[0.03] shadow-lg shadow-emerald-500/10'
                    : 'border-slate-800 hover:border-slate-700 bg-slate-900/40'
                }`}
              >
                {/* Visual indicator */}
                {isSelected && (
                  <span className="absolute top-4 right-4 text-emerald-400 animate-scaleIn">
                    <CheckCircle2 size={24} />
                  </span>
                )}

                <div className="flex items-start gap-4">
                  <div className={`p-3 rounded-xl transition-all ${
                    isSelected ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-800 text-slate-400 group-hover:text-slate-300'
                  }`}>
                    <Briefcase size={24} />
                  </div>
                  <div className="space-y-1 pr-6">
                    <h3 className="text-lg font-bold text-white">{role.name}</h3>
                    <p className="text-slate-400 text-xs leading-relaxed">{role.description}</p>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        <div className="flex justify-center">
          <button
            onClick={handleRunAnalysis}
            disabled={!selectedRoleId}
            className="w-full max-w-sm py-4 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 text-slate-950 font-bold rounded-lg shadow-lg hover:shadow-emerald-500/20 active:scale-[0.98] transition-all duration-200 text-sm flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Run Skill Gap Analysis
            <Target size={18} />
          </button>
        </div>
      </div>
    </div>
  );
};

export default RoleSelection;
