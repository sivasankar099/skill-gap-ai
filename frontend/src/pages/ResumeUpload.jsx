import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../services/api';
import { Upload, FileText, ChevronRight, AlertCircle, Sparkles, CheckCircle, BookOpen, Layers } from 'lucide-react';

export const ResumeUpload = () => {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [dragActive, setDragActive] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [extractedData, setExtractedData] = useState(null);

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    setError('');
    
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const droppedFile = e.dataTransfer.files[0];
      validateAndSetFile(droppedFile);
    }
  };

  const handleChange = (e) => {
    e.preventDefault();
    setError('');
    if (e.target.files && e.target.files[0]) {
      const selectedFile = e.target.files[0];
      validateAndSetFile(selectedFile);
    }
  };

  const validateAndSetFile = (selectedFile) => {
    if (selectedFile.type !== 'application/pdf' && !selectedFile.name.toLowerCase().endsWith('.pdf')) {
      setError('Only PDF resumes are supported');
      setFile(null);
      return;
    }
    if (selectedFile.size > 5 * 1024 * 1024) {
      setError('File size exceeds the 5MB limit');
      setFile(null);
      return;
    }
    setFile(selectedFile);
  };

  const handleUpload = async () => {
    if (!file) return;
    setError('');
    setLoading(true);

    try {
      const data = await apiService.uploadResume(file);
      setExtractedData(data);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to parse resume. Make sure it is not empty, scanned, or corrupted.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 p-6 flex flex-col items-center justify-center relative overflow-hidden">
      {/* Background radial effects */}
      <div className="absolute top-0 left-1/3 w-[500px] h-[500px] bg-emerald-500/5 rounded-full blur-3xl"></div>
      <div className="absolute bottom-0 right-1/3 w-[500px] h-[500px] bg-cyan-500/5 rounded-full blur-3xl"></div>

      <div className="w-full max-w-3xl relative z-10">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-extrabold tracking-tight text-white mb-2">
            Upload Your Resume
          </h1>
          <p className="text-slate-400 text-base">
            Upload your PDF resume to extract and normalize your professional skillset.
          </p>
        </div>

        {!extractedData ? (
          <div className="glass rounded-2xl p-8 space-y-6">
            {error && (
              <div className="flex items-center gap-3 text-rose-400 bg-rose-500/10 border border-rose-500/20 p-4 rounded-xl text-sm">
                <AlertCircle size={20} className="shrink-0" />
                <span>{error}</span>
              </div>
            )}

            {/* Drag and Drop Zone */}
            <div
              onDragEnter={handleDrag}
              onDragOver={handleDrag}
              onDragLeave={handleDrag}
              onDrop={handleDrop}
              className={`border-2 border-dashed rounded-xl p-12 flex flex-col items-center justify-center transition-all duration-200 relative ${
                dragActive ? 'border-emerald-400 bg-emerald-950/10' : 'border-slate-800 hover:border-slate-700 bg-slate-900/40'
              }`}
            >
              <input
                type="file"
                id="resume-upload"
                onChange={handleChange}
                className="hidden"
                accept=".pdf"
              />
              
              <Upload size={48} className={dragActive ? 'text-emerald-400 animate-bounce' : 'text-slate-500'} />
              
              <p className="mt-4 text-base font-semibold text-slate-300">
                Drag and drop your resume here, or{' '}
                <label htmlFor="resume-upload" className="text-emerald-400 hover:text-emerald-300 cursor-pointer underline transition-all">
                  browse files
                </label>
              </p>
              <p className="mt-2 text-xs text-slate-500">
                PDF format only. Maximum file size 5MB.
              </p>

              {file && (
                <div className="mt-6 flex items-center gap-3 bg-slate-800/80 border border-slate-700 px-4 py-2.5 rounded-lg">
                  <FileText className="text-emerald-400" size={18} />
                  <span className="text-sm font-medium text-slate-200 truncate max-w-xs">
                    {file.name}
                  </span>
                  <span className="text-xs text-slate-400">
                    ({(file.size / 1024).toFixed(1)} KB)
                  </span>
                </div>
              )}
            </div>

            <button
              onClick={handleUpload}
              disabled={!file || loading}
              className="w-full py-3 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 text-slate-950 font-bold rounded-lg shadow-lg hover:shadow-emerald-500/20 active:scale-[0.98] transition-all duration-200 text-sm flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-slate-950 border-t-transparent rounded-full animate-spin"></div>
              ) : (
                <>
                  <Sparkles size={18} />
                  Extract Resume Skills
                </>
              )}
            </button>
          </div>
        ) : (
          /* Structured Draft Display */
          <div className="glass rounded-2xl p-8 space-y-8 animate-fadeIn">
            <div className="flex items-center gap-3 bg-emerald-500/10 border border-emerald-500/20 p-4 rounded-xl text-emerald-400">
              <CheckCircle size={24} />
              <div>
                <h3 className="font-bold text-sm">Resume parsed successfully!</h3>
                <p className="text-xs text-emerald-400/80 mt-0.5">Please review the extracted data normalized by Gemini.</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Technical Skills */}
              <div className="bg-slate-900/60 border border-slate-800/80 rounded-xl p-5 space-y-3">
                <div className="flex items-center gap-2 text-emerald-400 font-semibold border-b border-slate-800 pb-2">
                  <Layers size={18} />
                  <h4>Technical Skills</h4>
                </div>
                <div className="flex flex-wrap gap-2">
                  {extractedData.technicalSkills?.length > 0 ? (
                    extractedData.technicalSkills.map((skill, idx) => (
                      <span key={idx} className="bg-slate-800 text-slate-300 text-xs px-2.5 py-1 rounded-md border border-slate-700/50">
                        {skill}
                      </span>
                    ))
                  ) : (
                    <span className="text-slate-500 text-xs italic">No technical skills detected</span>
                  )}
                </div>
              </div>

              {/* Soft Skills */}
              <div className="bg-slate-900/60 border border-slate-800/80 rounded-xl p-5 space-y-3">
                <div className="flex items-center gap-2 text-teal-400 font-semibold border-b border-slate-800 pb-2">
                  <BookOpen size={18} />
                  <h4>Soft Skills</h4>
                </div>
                <div className="flex flex-wrap gap-2">
                  {extractedData.softSkills?.length > 0 ? (
                    extractedData.softSkills.map((skill, idx) => (
                      <span key={idx} className="bg-slate-800 text-slate-300 text-xs px-2.5 py-1 rounded-md border border-slate-700/50">
                        {skill}
                      </span>
                    ))
                  ) : (
                    <span className="text-slate-500 text-xs italic">No soft skills detected</span>
                  )}
                </div>
              </div>
            </div>

            {/* Extracted Projects */}
            <div className="bg-slate-900/60 border border-slate-800/80 rounded-xl p-5 space-y-4">
              <h4 className="text-white font-semibold border-b border-slate-800 pb-2">Extracted Projects</h4>
              {extractedData.projects?.length > 0 ? (
                <div className="space-y-4">
                  {extractedData.projects.map((proj, idx) => (
                    <div key={idx} className="bg-slate-800/40 p-3 rounded-lg border border-slate-800">
                      <h5 className="text-emerald-400 text-sm font-semibold">{proj.name}</h5>
                      <p className="text-slate-400 text-xs mt-1 leading-relaxed">{proj.description}</p>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-slate-500 text-xs italic">No project listings found</p>
              )}
            </div>

            <button
              onClick={() => navigate('/select-role')}
              className="w-full py-3.5 bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-400 hover:to-teal-400 text-slate-950 font-bold rounded-lg shadow-lg hover:shadow-emerald-500/20 active:scale-[0.98] transition-all duration-200 text-sm flex items-center justify-center gap-2 cursor-pointer"
            >
              Next: Select Target Role
              <ChevronRight size={18} />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ResumeUpload;
